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

package com.openkoda.core.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

/**
 * Service for Amazon S3 {@link com.openkoda.model.file.File} storage
 * The Service by default exposes mock/empty beans for S3 services.
 * The actual services are activated when 'file.storage.type=amazon'
 */
@Configuration
public class AmazonS3Service {

    /**
     * @return Live S3 service client bean, activated when 'file.storage.type=amazon'
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage.type", havingValue = "amazon")
    public S3Client amazonS3() {
        return S3Client.create();
    }

    /**
     * @return Live S3 presigner bean, activated when 'file.storage.type=amazon'
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage.type", havingValue = "amazon")
    public S3Presigner amazonS3Presigner() {
        return S3Presigner.create();
    }

    /**
     * @return Mock S3 service client bean, privided when 'file.storage.type != amazon'
     */
    @Bean
    @Primary
    public S3Client emptyAmazonS3() {
        return new S3Client() {
            @Override
            public String serviceName() {return null;}
            @Override
            public void close() {}
        };
    }

    /**
     * @return Live S3 presigner bean, activated when 'file.storage.type != amazon'
     */
    @Bean
    @Primary
    public S3Presigner emptyAmazonS3Presigner() {
        return new S3Presigner() {
            @Override
            public PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request) {
                return null;
            }

            @Override
            public PresignedPutObjectRequest presignPutObject(PutObjectPresignRequest request) {
                return null;
            }

            @Override
            public PresignedCreateMultipartUploadRequest presignCreateMultipartUpload(CreateMultipartUploadPresignRequest request) {
                return null;
            }

            @Override
            public PresignedUploadPartRequest presignUploadPart(UploadPartPresignRequest request) {
                return null;
            }

            @Override
            public PresignedCompleteMultipartUploadRequest presignCompleteMultipartUpload(CompleteMultipartUploadPresignRequest request) {
                return null;
            }

            @Override
            public PresignedAbortMultipartUploadRequest presignAbortMultipartUpload(AbortMultipartUploadPresignRequest request) {
                return null;
            }

            @Override
            public void close() {

            }
        };
    }
}
