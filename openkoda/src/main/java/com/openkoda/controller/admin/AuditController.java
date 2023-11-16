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

package com.openkoda.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.core.controller.generic.AbstractController._AUDIT;
import static com.openkoda.core.controller.generic.AbstractController._HTML;
import static com.openkoda.core.security.HasSecurityRules.CHECK_CAN_READ_SUPPORT_DATA;

/**
 * <p>AuditController class.</p>
 * <p>Controller for Audit related actions.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */

@Controller
@RequestMapping(_HTML + _AUDIT)
public class AuditController extends AbstractAuditController {

    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    @GetMapping(_ALL)
    public Object getAll(
            @SortDefault(sort = ID, direction = Sort.Direction.DESC)
            @Qualifier("audit") Pageable auditPageable,
            @RequestParam(required = false, defaultValue = "", name = "audit_search") String search,
            HttpServletRequest request) {
        debug("[getAll] search: {}", search);
        return  findAll(auditPageable, search)
            .mav("audit-all");
    }


    @GetMapping(_ID + _CONTENT)
    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    @ResponseBody
    public String downloadContent(@PathVariable(ID) Long auditId) {
        debug("[downloadContent] auditId: {}", auditId);
        return repositories.secure.audit.findOne(auditId).getContent();
    }

}
