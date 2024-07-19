/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

package com.openkoda.controller.frontendresource;

import com.openkoda.controller.file.AbstractFileController;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.Privilege;
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
public class FrontendResourceAssetController extends AbstractFileController implements HasSecurityRules {

    @Transactional(readOnly = true)
    @GetMapping(value = {FILE_ASSET + "{frontendResourceFileId:" + NUMBERREGEX + "$}/*",
            _HTML + FILE_ASSET + "{frontendResourceFileId:" + NUMBERREGEX + "$}/*"})
    public void getFrontendResourceAsset(@PathVariable("frontendResourceFileId") Long frontendResourceFileId,
                                         @RequestParam(name = "dl", required = false, defaultValue = "false") boolean download,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException, SQLException {
        debug("[getFrontendResourceAsset] frontendResourceFileId {}", frontendResourceFileId);
        File f = request.getRequestURI().contains(_HTML) && hasGlobalPrivilege(Privilege.isUser) ?
                repositories.unsecure.file.findOne(frontendResourceFileId)
                : repositories.unsecure.file.findByIdAndPublicFileTrue(frontendResourceFileId);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        services.file.getFileContentAndPrepareResponse(f, download, true, response);
    }
}
