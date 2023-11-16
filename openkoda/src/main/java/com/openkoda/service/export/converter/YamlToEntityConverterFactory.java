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

package com.openkoda.service.export.converter;

import com.openkoda.core.flow.LoggingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YamlToEntityConverterFactory implements LoggingComponent {
    private static Map<Class<?>, YamlToEntityConverter<?, ?>> parentConverters = new HashMap<>();

    @Autowired
    public YamlToEntityConverterFactory(List<YamlToEntityConverter<?, ?>> converterList) {
        for (YamlToEntityConverter<?, ?> converter : converterList) {
            YamlToEntityParentConverter annotation = converter.getClass().getAnnotation(YamlToEntityParentConverter.class);
            if (annotation != null) {
                parentConverters.put(annotation.dtoClass(), converter);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T, D> T processYamlDto(D dto, String filePath) {
        debug("[processYamlDto]");

        if (dto == null) {
            return null;
        }

        YamlToEntityConverter<T, D> converter = (YamlToEntityConverter<T, D>) parentConverters.get(dto.getClass());

        if (converter == null) {
            throw new IllegalArgumentException("No parent converter found for DTO class: " + dto.getClass().getName());
        }
        debug("[processYamlDto] Converting dto: " + dto.getClass().getName());
        return converter.convertAndSave(dto, filePath);
    }
}