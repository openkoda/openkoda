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

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.form.LoggerForm;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

/**
 * <p>Controller that provides actual Logs related functionality</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 */
public class AbstractAdminLogsController extends AbstractController {

    protected PageModelMap getLogsFlow() {
        debug("[getLogsFlow]");
        return Flow.init()
                .thenSet(logsEntryList, a -> services.logConfig.getDebugEntriesAsList())
                .execute();
    }


    protected PageModelMap getSettingsFlow() {
        debug("[getSettingsFlow]");
        return Flow.init()
                .thenSet(loggerForm, a -> new LoggerForm(services.logConfig.getDebugLoggers(), services.logConfig.getMaxEntries()))
                .execute();
    }

    protected PageModelMap saveSettings(LoggerForm loggerFormData, BindingResult br) {
        debug("[saveSettings]");
        return Flow.init(loggerForm, loggerFormData)
                .thenSet(logClassNamesList, a -> services.logConfig.getAvailableLoggers().stream().map(Class::getName).collect(Collectors.toList()))
                .then(a -> services.validation.validate(loggerFormData, br))
                .then(a -> services.logConfig.saveConfig(loggerFormData.dto.getBufferSize(), loggerFormData.dto.getLoggingClasses()))
                .execute();
    }
}
