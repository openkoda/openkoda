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

import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.LoggerForm;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._HTML;
import static com.openkoda.controller.common.URLConstants._LOGS;

@RestController
/**
 * <p>AdminLogsController class.</p>
 * <p>Controller for all purely Admin related actions. Eg. General configuration fo the system.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@RequestMapping({_HTML + _LOGS})
public class AdminLogsController extends AbstractAdminLogsController implements HasSecurityRules {

    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    @GetMapping(value = _DOWNLOAD)
    @ResponseBody
    public Object downloadLogs() {
        debug("[downloadLogs]");
        return services.logConfig.getDebugEntries();
    }

    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    @GetMapping(value = _ALL)
    @ResponseBody
    public Object showLogs() {
        debug("[showLogs]");
        return getLogsFlow()
                .mav(LOGS + "-" + ALL);
    }

    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    @GetMapping(value = _SETTINGS)
    public Object getSettings() {
        debug("[getSettings]");
        return getSettingsFlow()
                .mav(LOGS + "-settings");
    }

    @PreAuthorize(CHECK_CAN_MANAGE_SUPPORT_DATA)
    @PostMapping(value = _SETTINGS)
    public Object setSettings(@Valid LoggerForm loggerFormData, BindingResult br) {
        debug("[setSettings]");
        return saveSettings(loggerFormData, br)
                .mav(ENTITY + '-' + FORMS + "::logger-settings-form-success",
                        ENTITY + '-' + FORMS + "::logger-settings-form-error");
    }

}
