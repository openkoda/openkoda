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

package com.openkoda.form;

import com.openkoda.core.form.AbstractForm;
import com.openkoda.dto.system.LoggerDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Set;
import java.util.stream.Collectors;

public class LoggerForm extends AbstractForm<LoggerDto> {

    public LoggerForm() {
        super(new LoggerDto(), FrontendMappingDefinitions.loggerForm);
    }

    public LoggerForm(Set<Class> debugLoggers, int maxEntries) {
        super(new LoggerDto(), FrontendMappingDefinitions.loggerForm);
        dto.bufferSizeField = String.valueOf(maxEntries);
        dto.loggingClasses = debugLoggers.stream().map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public LoggerForm validate(BindingResult br) {
        if (StringUtils.isBlank(dto.bufferSizeField)) {
            br.rejectValue("dto.bufferSizeField", "not.empty");
        }
        if (!StringUtils.isBlank(dto.bufferSizeField) && !dto.bufferSizeField.matches("\\d+")) {
            br.rejectValue("dto.bufferSizeField", "is.number");
        }
        return this;
    }

}
