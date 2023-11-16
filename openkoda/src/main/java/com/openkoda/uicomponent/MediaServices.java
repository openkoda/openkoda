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

package com.openkoda.uicomponent;

import com.openkoda.model.file.File;

import java.io.InputStream;
import java.util.Map;

/**
 * Provides for media creation, ie. files, images, movies, documents
 */
public interface MediaServices {

    /**
     * <p>Creates a pdf file based on templateName template and returns it as byte array.</p>
     * <p>Many pdf pages can be created, with separate pages for each model provided</p>
     * <p>When models is null or empty, the template will be fed with empty model and one pdf will be generated</p>
     */
    byte[] writePdfToByteArray(String templateName, Map<String, Object> ... models);

    /**
     * <p>Creates a pdf file based on templateName template and returns it input stream</p>
     * <p>Many pdf pages can be created, with separate pages for each model provided</p>
     * <p>When models is null or empty, the template will be fed with empty model and one pdf will be generated</p>
     */
    InputStream writePdfToStream(String templateName, Map<String, Object> ... models);

    /**
     * <p>Creates a file based from input stream</p>
     * <p>It ties to determine the content type of the file automatically</p>
     * <p>The method requires to provide content size which may be difficult in some scenarios.</p>
     */
    File createFileFromStream(InputStream inputStream, long totalFileSize, String fileName);

    /**
     * <p>Creates a file based from byte array</p>
     * <p>It ties to determine the content type of the file automatically</p>
     */
    File createFileFromByteArray(byte[] input, String fileName);

}
