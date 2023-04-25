/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.service;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.flow.ValidationException;
import com.openkoda.model.file.File;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Duration;

import static com.openkoda.core.service.FileService.StorageType.database;
import static com.openkoda.core.service.FileService.StorageType.filesystem;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@Service("file")
public class FileService extends ComponentProvider {

    public enum StorageType {
        database, filesystem, amazon
    }

    @Value("${spring.http.multipart.max-file-size}")
    private String maxUploadSizeInBytesExpression;
    private Long maxUploadSizeInBytes;

    @Value("${file.storage.type:database}")
    StorageType storageType;

    @Value("${file.storage.filesystem.path:/tmp}")
    private String storageFilesystemPath;

    @Value("${file.storage.amazon.bucket:bucket-name}")
    private String storageAmazonBucket;

    @Value("${file.storage.amazon.presigned-url.expiry.time.seconds:10}")
    private long amazonPresignedUrlExpiryTimeInSeconds;

    @PostConstruct
    private void init() {
        String fixedExpression = maxUploadSizeInBytesExpression
                .replace("GB", " * 1024 * 1024 * 1024")
                .replace("MB", " * 1024 * 1024")
                .replace("kB", " * 1024");
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(fixedExpression);
        maxUploadSizeInBytes = exp.getValue(Long.class);
    }

    public Long getMaxUploadSizeInBytes() {
        return maxUploadSizeInBytes;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public String getStorageFilesystemPath() {
        return storageFilesystemPath;
    }

    public String prepareStoredFileName(Long orgId, String uuid, String fileName) {
        debug("[prepareStoredFileName]");
        String name = (orgId == null ? 0L : orgId) + "-" + uuid + "-" + fileName;
        return FilenameUtils.concat(storageFilesystemPath, name);
    }

    public String prepareFileNameForS3Storage(Long orgId, String uuid, String fileName) {
        debug("[prepareFileNameForS3Storage]");
        return (orgId == null ? 0L : orgId) + "/" + uuid + "-" + fileName;
    }

    public File scaleImage(File in, int width) {
        debug("[scaleImage]");
        if (in == null || !in.isImage()) {
            throw new ValidationException("Null or not an image");
        }
        try {
            BufferedImage bi = ImageIO.read(in.getContentStream());
            int originalWidth = bi.getWidth();
            int originalHeight = bi.getHeight();
            if (width > originalWidth) {
                throw new ValidationException("Failed. Image's width is less than " + width + ", so there is no point to scale.");
            }
            int height = (int) ((((double) originalHeight) / originalWidth) * width);

            Image resultImage = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage buffered = new BufferedImage(width, height, TYPE_INT_RGB);
            buffered.getGraphics().drawImage(resultImage, 0, 0, null);

            java.io.File temporaryFile = java.io.File.createTempFile("scale", "image");
            ImageIO.write(buffered, "jpg", temporaryFile);

            long fileSize = FileUtils.sizeOf(temporaryFile);
            int lastDotIndex = in.getFilename().lastIndexOf(".");
            String filename = in.getFilename().substring(0, lastDotIndex) + "-" + width + in.getFilename().substring(lastDotIndex);
            try (FileInputStream fis = new FileInputStream(temporaryFile)) {
                MultipartFile multipartFile = new MockMultipartFile(filename, IOUtils.toByteArray(fis));
                return saveAndPrepareFileEntity(in.getOrganizationId(), null, filename, fileSize, filename, multipartFile.getInputStream());
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public File saveAndPrepareFileEntity(Long orgId, String uuid, String fileName, long totalFileSize, String originalFilename, InputStream inputStream) throws IOException {
        debug("[saveAndPrepareFileEntity]");
        File f = null;
        Path path = new java.io.File(originalFilename).toPath();
        String mimeType = Files.probeContentType(path);
        FileService.StorageType actualStorageType = getStorageType();
        if (actualStorageType == filesystem) {
            String targetFileName = prepareStoredFileName(orgId, uuid, fileName);
            try (FileOutputStream fileOnDisk = new FileOutputStream(targetFileName)) {
                IOUtils.copy(inputStream, fileOnDisk);
            }
            f = new File(orgId, originalFilename, mimeType, totalFileSize, uuid, actualStorageType, targetFileName);
        } else if (actualStorageType == database) {
            Blob b = BlobProxy.generateProxy(inputStream, totalFileSize);
            f = new File(orgId, originalFilename, mimeType, totalFileSize, uuid, actualStorageType);
            f.setContent(b);
        }
        return f;
    }

    public String getPresignedUrlToFileFromAws(File f) {
        debug("[getPresignedUrlToFileFromAws]");
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(storageAmazonBucket)
                .key(f.getFilesystemPath())
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(amazonPresignedUrlExpiryTimeInSeconds))
                .getObjectRequest(getObjectRequest)
                .build();
        try {
            PresignedGetObjectRequest presignedGetObjectRequest = services.amazonS3Presigner.presignGetObject(getObjectPresignRequest);
            return presignedGetObjectRequest.url().toString();
        } catch (NoSuchKeyException | InvalidObjectStateException e) {
            error(e, "[getPresignedUrlToFileFromAws] {} thrown while trying to get presigned URL to fileId {}. " +
                    "File is either deleted or archived on AWS S3", e.getClass().getName(), f.getId());
            throw new RuntimeException("[getPresignedUrlToFileFromAws] " + e.getClass().getName()
                    + " thrown while trying to get presigned URL to fileId " + f.getId() + ". File is either deleted or archived on AWS S3");
        } catch (SdkException e) {
            error(e, "[getPresignedUrlToFileFromAws] {} thrown while trying to get presigned URL to fileId {}", e.getClass().getName(), f.getId());
            throw new RuntimeException("[getPresignedUrlToFileFromAws] " + e.getClass().getName()
                    + " thrown while trying to get presigned URL to fileId " + f.getId());
        }
    }
}
