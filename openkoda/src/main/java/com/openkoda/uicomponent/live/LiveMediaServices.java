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

package com.openkoda.uicomponent.live;

import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.service.FileService;
import com.openkoda.core.service.pdf.PdfConstructor;
import com.openkoda.model.file.File;
import com.openkoda.repository.file.SecureFileRepository;
import com.openkoda.uicomponent.MediaServices;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;


/**
 * Live implementation of {@link MediaServices}
 */
@Component
public class LiveMediaServices implements MediaServices {
    @Inject
    PdfConstructor pdfConstructor;

    @Inject
    FileService fileService;

    @Inject
    SecureFileRepository fileRepository;

    @Override
    public byte[] writePdfToByteArray(String templateName, Map<String, Object> ... models) {
        return pdfConstructor.writeDocumentToByteArray(templateName, models);
    }

    @Override
    public InputStream writePdfToStream(String templateName, Map<String, Object> ... models) {
        return pdfConstructor.writeDocumentToStream(templateName, models);
    }

    @Override
    public File createFileFromByteArray(byte[] input, String fileName) {
        File f = null;
        try {
            f = fileService.saveAndPrepareFileEntity(
                    TenantResolver.getTenantedResource().organizationId,
                    null, fileName, fileName, input);
            f = fileRepository.save(f);
            return f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File createFileFromStream(InputStream inputStream, long totalFileSize, String fileName) {
        try {
            File f = fileService.saveAndPrepareFileEntity(
                    TenantResolver.getTenantedResource().organizationId,
                    null, fileName, totalFileSize, fileName, inputStream);
            f = fileRepository.save(f);
            return f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}