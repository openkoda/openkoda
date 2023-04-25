/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.controller;

import com.openkoda.controller.file.AbstractFileController;
import com.openkoda.model.file.File;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;

@Controller
public class FrontendResourceAssetController extends AbstractFileController {

    @Transactional(readOnly = true)
    @GetMapping(value = "/frontend-resource-asset-{frontendResourceFileId:" + NUMBERREGEX + "$}/*")
    public void getFrontendResourceAsset(@PathVariable("frontendResourceFileId") Long frontendResourceFileId, @RequestParam(name = "dl", required = false, defaultValue = "false") boolean download, HttpServletRequest request,
                            HttpServletResponse response) throws IOException, SQLException {
        debug("[getFrontendResourceAsset] frontendResourceFileId {}", frontendResourceFileId);
        File f = repositories.unsecure.file.findByIdAndPublicFileTrue(frontendResourceFileId);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        getFileContentAndPrepareResponse(f, download, response);
    }
}
