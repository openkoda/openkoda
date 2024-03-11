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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.component.FrontendResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService implements LoggingComponentWithRequestId {

    public ByteArrayOutputStream zipFrontendResources(List<FrontendResource> resources) {
        debug("[zipFrontendResources]");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (FrontendResource resource : resources) {
                String fileName = resource.getName().replace(" ", "_") + ".html";
                zos.putNextEntry(new ZipEntry(fileName));
                zos.write(resource.getContent().getBytes(Charset.defaultCharset()));
                zos.closeEntry();
                if(resource.getDraftContent() != null) {
                    zos.putNextEntry(new ZipEntry("draft_" + fileName));
                    zos.write(resource.getDraftContent().getBytes(Charset.defaultCharset()));
                    zos.closeEntry();
                }
            }

        } catch (IOException e) {
            error("[zipFrontendResources]", e);
        }
        return baos;
    }

    public ByteArrayOutputStream zipByteInput(InputStream bis, String inputFileName) {
        debug("[zipByteInput] inputFileName: {}", inputFileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            int size = bis.available();
            while (size > 0) {
                size = size > 1024 ? 1024 : size;
                byte[] data = new byte[size];
                zos.putNextEntry(new ZipEntry(inputFileName));
                zos.write(bis.read(data));
                size = bis.available();
            }
            zos.closeEntry();


        } catch (IOException e) {
            error("[zipByteInput]", e);
        }
        return baos;
    }

    public ByteArrayOutputStream zipByteArray(byte[] data, String inputFileName) {
        debug("[zipByteArray] inputFileName: {}", inputFileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(inputFileName));
            zos.write(data);
            zos.closeEntry();
        } catch (IOException e) {
            error("[zipByteArray]", e);
        }
        return baos;
    }
}
