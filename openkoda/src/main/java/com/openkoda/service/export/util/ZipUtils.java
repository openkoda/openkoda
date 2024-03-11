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

package com.openkoda.service.export.util;

import com.openkoda.core.flow.LoggingComponent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipUtils implements LoggingComponent {
    public void addToZipFile(String content, String entryName, ZipOutputStream zipOut){
        debug("[addToZipFile]");

        try {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            if(StringUtils.hasText(content)) {
                zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            }
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Zip export of " + entryName + " failed");
        }
    }

    public void addFileToZip(File file, String entryName, ZipOutputStream zipOut){
        debug("[addFileToZip]");

        try {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        } catch (IOException e) {
            error("[addFileToZip]", e);
        }
    }

    public void addURLFileToZip(URL url, String entryName, ZipOutputStream zipOut){
        debug("[addURLFileToZip]");

        try {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            InputStream is = url.openStream();
            byte[] bytes = new byte[1024];
            int length;
            while((length = is.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            is.close();
        } catch (IOException e) {
            error("[addURLFileToZip]", e);
        }
    }

    public String setResourceFilePath(String filePath, String entityName, Long organizationId){
        debug("[setResourceFilePath]");

        return organizationId == null ? String.format("%s%s.yaml",filePath, entityName) : String.format("%s%s_%s.yaml", filePath, entityName, organizationId);
    }
}
