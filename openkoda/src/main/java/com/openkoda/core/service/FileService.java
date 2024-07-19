/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.*;

import static com.openkoda.core.service.FileService.StorageType.database;
import static com.openkoda.core.service.FileService.StorageType.filesystem;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@Service("file")
public class FileService extends ComponentProvider {

    public enum StorageType {
        database, filesystem, amazon
    }

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSizeInBytesExpression;
    private Long maxUploadSizeInBytes;

    @Value("${file.storage.type:database}")
    StorageType storageType;

    @Value("${file.storage.filesystem.path:/tmp}")
    private String storageFilesystemPath;

    @Value("${file.storage.filesystem.failover:/tmp2}")
    private String failoverStorageFilesystemPath;

    @Value("${file.storage.filesystem.failover.writable:false}")
    private boolean writableFailoverStoreageFilesystem;

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
            BufferedImage bi = tryInputOutput(() -> ImageIO.read(in.getContentStream()));
            if (bi == null) {
                error("[saveAndPrepareFileEntity] Error while attempting to read and scale image [{}] ", in.toAuditString());
                String failoverPath = in.getFilesystemPath().replaceFirst(storageFilesystemPath, failoverStorageFilesystemPath);
                bi = tryInputOutput(() -> ImageIO.read(new FileInputStream(failoverPath)));
                if (bi == null) {
                    error("[saveAndPrepareFileEntity] Error while attempting failover to read and scale image [{}] ", in.toAuditString());
                    return null;
                }
            }
            
            int originalWidth = bi.getWidth();
            int originalHeight = bi.getHeight();
            if (width > originalWidth) {
                throw new ValidationException("Failed. Image's width is less than " + width + ", so there is no point to scale.");
            }
            int height = (int) ((((double) originalHeight) / originalWidth) * width);

            Image resultImage = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage buffered = new BufferedImage(width, height, TYPE_INT_RGB);
            buffered.getGraphics().drawImage(resultImage, 0, 0, null);
            
            //create in memory only input/outptu streams to avoid dangling unused temporary images in containerized (docker) context
            FastByteArrayOutputStream os = new FastByteArrayOutputStream(buffered.getData().getDataBuffer().getSize() / 8);
            String ext = in.getFilename().substring(in.getFilename().lastIndexOf('.') + 1).toLowerCase();
            ImageIO.write(buffered, ext, os);

            long fileSize = os.size();
            int lastDotIndex = in.getFilename().lastIndexOf(".");
            String filename = in.getFilename().substring(0, lastDotIndex) + "-" + width + in.getFilename().substring(lastDotIndex);
            MultipartFile multipartFile = new MockMultipartFile(filename, os.toByteArrayUnsafe());
            return saveAndPrepareFileEntity(in.getOrganizationId(), null, filename, fileSize, filename, multipartFile.getInputStream());
            
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public File saveAndPrepareFileEntity(Long orgId, String uuid, String fileName, String originalFilename, byte[] input) throws IOException, SQLException {
        return saveAndPrepareFileEntity(orgId, uuid, fileName, Long.valueOf(input.length), originalFilename, new ByteArrayInputStream(input));
    }

    public File saveAndPrepareFileEntity(Long orgId, String uuid, String fileName, long totalFileSize, String originalFilename, InputStream inputStream) throws IOException, SQLException {
        debug("[saveAndPrepareFileEntity]");
        File f = null;
        Path path = new java.io.File(originalFilename).toPath();
        String mimeType = Files.probeContentType(path);
        StorageType actualStorageType = getStorageType();
        if (actualStorageType == filesystem) {
            f = handleFilesystemWrite(orgId, uuid, fileName, totalFileSize, originalFilename, inputStream, f, mimeType,
                    actualStorageType);
        } else if (actualStorageType == database) {
            Blob b = BlobProxy.generateProxy(inputStream, totalFileSize);
            f = new File(orgId, originalFilename, mimeType, totalFileSize, uuid, actualStorageType);
            f.setContent(b);
        }
        return f;
    }

    private File handleFilesystemWrite(Long orgId, String uuid, String fileName, long totalFileSize,
            String originalFilename, InputStream inputStream, File f, String mimeType, StorageType actualStorageType) {
        String targetFileName = prepareStoredFileName(orgId, uuid, fileName);
        var ioResult = tryIOOperation(() -> {
            try (FileOutputStream fileOnDisk = new FileOutputStream(targetFileName)) {
                IOUtils.copy(inputStream, fileOnDisk);
            }
        });
                
        if(!ioResult) {
            error("[saveAndPrepareFileEntity] Error while attempting to write [{}, {}, {}] [{}]", orgId, uuid, fileName, targetFileName);
            
            if(writableFailoverStoreageFilesystem) {
                String failoverPath = targetFileName.replaceFirst(storageFilesystemPath, failoverStorageFilesystemPath);
                ioResult = tryIOOperation(() -> {
                    try (FileOutputStream fileOnDisk = new FileOutputStream(failoverPath)) {
                        IOUtils.copy(inputStream, fileOnDisk);
                    }
                });
                
                if(!ioResult) {
                    error("[saveAndPrepareFileEntity] Error while attempting to write to failover path [{}, {}, {}] [{}]}", orgId, uuid, fileName, targetFileName);
                }
            }
        }
        
        if(ioResult) {
            f = new File(orgId, originalFilename, mimeType, totalFileSize, uuid, actualStorageType, targetFileName);
        }
        return f;
    }
    
    //public for access in tests in FileServiceTest, because this method was in FileService
    //and creating test class for this controller creates problem with setting FileService.storageType in test cases
    public HttpServletResponse getFileContentAndPrepareResponse(File f, boolean download, boolean allowCache, HttpServletResponse response) throws IOException, SQLException {
        debug("[getFileContentAndPrepareResponse] fileId: {}", f.getId());
        response.addHeader("Content-Type", f.getContentType());
        response.addHeader("Content-Length", Long.toString(f.getSize()));
        if (download) response.addHeader("Content-Disposition", "attachment; filename=\"" + f.getFilename() + "\"");
        LocalDateTime updatedOn = f.getUpdatedOn() == null ? LocalDateTime.now() : f.getUpdatedOn();
        response.addDateHeader("Last-Modified", updatedOn.toEpochSecond(ZoneOffset.UTC) * 1000);
        OutputStream os = response.getOutputStream();
        StorageType storageType = f.getStorageType();
        if (storageType == filesystem || storageType == database) {
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("Cache-Control", allowCache ? "max-age=604800, public" : "no-store, no-cache, must-revalidate");
        }
        
        if (storageType == filesystem) {
            handleFilesystemRead(f, os);
        } else if(storageType == database) {
            try (InputStream is = f.getContentStream()) {
                IOUtils.copy(is, os);
            }   
        }
              
        os.flush();
        return response;
    }

    private void handleFilesystemRead(File f, OutputStream os) {
         var ioResult = tryIOOperation( () -> {
            try (InputStream is = f.getContentStream()) {
                IOUtils.copy(is, os);
            }
        });
        
        if(!ioResult) {
            error("[saveAndPrepareFileEntity] Error while attempting to read [{}] ", f.toAuditString());
            String failoverPath = f.getFilesystemPath().replaceFirst(storageFilesystemPath, failoverStorageFilesystemPath);
            ioResult = tryIOOperation( () -> {
                try (InputStream failoverStream = new FileInputStream(failoverPath)) {
                    IOUtils.copy(failoverStream, os);
                }
            });
            
            if(!ioResult) {
                error("[saveAndPrepareFileEntity] Error while attempting failover to read [{}] ", f.toAuditString());
            }
        }
    }
    
    private interface ThrowableRunnable {
        public abstract void run() throws IOException, SQLException, RuntimeException;
    }

    private interface ThrowableSupplier<E> {
        public abstract E get() throws IOException, SQLException, RuntimeException;
    }
    
    /**
     * attempts to perform IO operation. After a fixed number of seconds OR a IOException it attempts to perform write to a fallback location
     * 
     * @param action
     * @return
     */
    protected boolean tryIOOperation(ThrowableRunnable action) {
        boolean result = false;
        Future<Boolean> future = CompletableFuture.supplyAsync( () -> {
            try {
                action.run();
                return true;
            } catch (IOException iexc) {
                error("[tryIOOperation] IO Error {}", iexc.toString());
                return false;
            } catch (SQLException | RuntimeException e) {
                // can't happen for fs storage
                return false;
            }
        });
        
        try {
            // actual system I/O timeout may occur after several seconds or even can cause Java thread be indefinitely blocked 
            result = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            error("[tryInputOutput] {}", e);
            throw new RuntimeException(e);
        }
        
        return result;
    }
    
    protected <E> E  tryInputOutput(ThrowableSupplier<E> supplier) {
        E result = null;
        Future<E> future = CompletableFuture.supplyAsync( () -> {
            try {
                return supplier.get();
            } catch (IOException iexc) {
                error("[tryIOOperation] IO Error {}", iexc.toString());
                return null;
            } catch (SQLException | RuntimeException e) {
                // can't happen for fs storage
                return null;
            }
        });
        
        try {
            // actual system I/O timeout may occur after several seconds or even can cause Java thread be indefinitely blocked 
            result = future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            error("[tryInputOutput] {}", e);
            throw new RuntimeException(e);
        }
        
        return result;
    }
    
    private class MockMultipartFile implements MultipartFile {

        private final String name;

        private final String originalFilename;

        @Nullable
        private final String contentType;

        private final byte[] content;

        public MockMultipartFile(String name, @Nullable byte[] content) {
            this(name, "", null, content);
        }

        public MockMultipartFile(String name, InputStream contentStream) throws IOException {
            this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
        }

        public MockMultipartFile(
                String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {

            Assert.hasLength(name, "Name must not be empty");
            this.name = name;
            this.originalFilename = (originalFilename != null ? originalFilename : "");
            this.contentType = contentType;
            this.content = (content != null ? content : new byte[0]);
        }

        public MockMultipartFile(
                String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
                throws IOException {

            this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
        }


        @Override
        public String getName() {
            return this.name;
        }

        @Override
        @NonNull
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        @Nullable
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return (this.content.length == 0);
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return this.content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.content);
        }

        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.content, dest);
        }

    }
}
