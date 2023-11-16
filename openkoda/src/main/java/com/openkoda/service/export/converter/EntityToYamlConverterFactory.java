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

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Component
public class EntityToYamlConverterFactory implements LoggingComponent {

    private final Map<Class<?>, EntityToYamlConverter<?, ?>> converterMap;

    @Autowired
    public EntityToYamlConverterFactory(List<EntityToYamlConverter<?, ?>> converters) {
        this.converterMap = converters.stream()
                .collect(Collectors.toMap(converter -> getEntityType(converter), Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public ZipOutputStream processEntityToYaml(Object entity, ZipOutputStream zipOut) {
        debug("[processEntityToYaml]");

        EntityToYamlConverter<Object, Object> converter = (EntityToYamlConverter<Object, Object>) converterMap.get(entity.getClass());

        if(converter == null){
            throw new IllegalArgumentException("No parent converter found for entity " + entity.getClass().getName());
        }
        converter.exportToYamlAndAddToZip(entity, zipOut);

        return zipOut;
    }
    private static Class<?> getEntityType(EntityToYamlConverter<?, ?> converter) {
        ParameterizedType type = (ParameterizedType) converter.getClass().getGenericInterfaces()[0];
        return (Class<?>) type.getActualTypeArguments()[0];
    }
}
