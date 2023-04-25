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

package com.openkoda.uicomponent.preview;

import com.openkoda.model.Organization;
import com.openkoda.model.User;
import com.openkoda.model.file.File;

class PreviewModel {
  static final User user1 = new User();
  static final User user2 = new User();
  static final Organization organization1 = new Organization();
  static final Organization organization2 = new Organization();
  static final File file1 = new File();
  static final File file2 = new File();

  static {
    user1.setFirstName("Józek");
    user1.setLastName("Kowalski");
    user1.setEmail("jozek@kowalski.com");

    user2.setFirstName("Maryla");
    user2.setLastName("Nowak");
    user2.setEmail("maryla@nowak.com");

    organization1.setId(100L);
    organization1.setName("Kovvalsky");

    organization2.setId(101L);
    organization2.setName("Nowakowa");

    file1.setFilename("file-1");
//    InputStream resourceAsStream = PreviewModel.class.getClassLoader().getResourceAsStream("/public/vendor/swagger-ui/springfox-swagger-ui/scan-repeat-large.png");
//    java.io.File f1 = new java.io.File("src/main/resources/public/vendor/swagger-ui/springfox-swagger-ui/scan-repeat-large.png");
//    try {
//      file1.setContent(BlobProxy.generateProxy(resourceAsStream.readAllBytes()));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    file1.setContentType(ContentType.IMAGE_PNG.getMimeType());
//    file1.setStorageType(FileService.StorageType.database);

    file2.setFilename("file-2");
  }
}
