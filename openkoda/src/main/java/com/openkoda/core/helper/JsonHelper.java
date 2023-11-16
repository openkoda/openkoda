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

package com.openkoda.core.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


/**
 * <p>Json helper provides a set of static methods for handling JSON formats.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Component("json")
public class JsonHelper implements LoggingComponentWithRequestId {

    private static final ObjectMapper om = new ObjectMapper();
    private static final String keyClassSeparator = "@";


    public static class KeyWithClassSerializer extends JsonSerializer<PageModelMap> {

        @Override
        public void serialize(PageModelMap value, JsonGenerator jgen, SerializerProvider provider) throws IOException, org.codehaus.jackson.JsonProcessingException {
            jgen.writeStartObject();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                jgen.writeFieldName(e.getKey() + (e.getValue() == null ? "" : keyClassSeparator + e.getValue().getClass().getCanonicalName()));
                jgen.writeObject(e.getValue());
            }
            jgen.writeEndObject();
        }
    }

    public static class KeyWithClassDeserializer extends JsonDeserializer<PageModelMap> {

        @Override
        public PageModelMap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            PageModelMap result = new PageModelMap();
            for (Iterator<Map.Entry<String, JsonNode>> i = node.fields(); i.hasNext(); ) {
                Map.Entry<String, JsonNode> f = i.next();
                String[] fieldNameAndClass = StringUtils.split(f.getKey(), keyClassSeparator);
                if (fieldNameAndClass.length == 1) {
                    result.put(fieldNameAndClass[0], om.convertValue(f.getValue(), Object.class));
                } else {
                    try {
                        Class c = ctxt.findClass(fieldNameAndClass[1]);
                        Object v = om.convertValue(f.getValue(), c);
                        result.put(fieldNameAndClass[0], v);
                    } catch (ClassNotFoundException e) {
                        LoggingComponent.debugLogger.debug("[deserialize]", e);
                    }
                }
            }
            return result;
        }

    }

    static {
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        om.registerModule(new JavaTimeModule());
        om.enable(MapperFeature.USE_ANNOTATIONS);
        SimpleModule module = new SimpleModule();
        module.addSerializer(PageModelMap.class, new KeyWithClassSerializer());
        module.addDeserializer(PageModelMap.class, new KeyWithClassDeserializer());
        om.registerModule(module);
    }

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static <T> T from(String jsonString, Class<T> t) {
        return gson.fromJson(jsonString, t);
    }

    public static <T> String to(T object) {
        return gson.toJson(object);
    }

    /**
     * <p>Method checking if {@link String} object is in Json format</p>
     */
    public static boolean looksLikeJsonObject(String jsonInput) throws IOException {
        if (StringUtils.isBlank(jsonInput)) {
            return false;
        }
        String trimmed = jsonInput.trim();
        return trimmed.charAt(0) == '{' && trimmed.charAt(trimmed.length() - 1) == '}';
    }

    /**
     * <p>Method converting {@link String} in json format into {@link PageModelMap} object</p>
     */
    public static PageModelMap fromDebugJson(String jsonInput) throws IOException {
        if (StringUtils.isBlank(jsonInput)) {
            return new PageModelMap();
        }
        TypeReference<PageModelMap> typeRef = new TypeReference<PageModelMap>() {};
        PageModelMap result;
        result = om.readValue(jsonInput, typeRef);
        return result;
    }

    /**
     * <p>Method debugging and converting {@link Map} into {@link String}</p>
     */
    public static String toDebugJson(Map<String, Object> model) {

        PageModelMap pageModelMap = new PageModelMap();

        for (Map.Entry<String, Object> e : model.entrySet()) {
            if (PageAttr.getByName(e.getKey()) != null) {
                pageModelMap.put(e.getKey(), e.getValue());
            }
        }

        //model that contains model itself causes circular serialization, so removing
        pageModelMap.remove(PageAttributes.modelAndView);

        try {
            return om.writeValueAsString(pageModelMap);
        } catch (JsonProcessingException e) {
            return "Error " + e.getMessage();
        }
    }

    /**
     * <p>Method converting map into sting in json format</p>
     *
     * @param map
     * @return a {@link String} object in json format
     */
    public static String formMapToJson(Map<String, String> map) {
        String json = "{";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json += "\"" + entry.getKey().replace("\"", "\\\"").replaceAll("dto\\.","") + "\"" + ":" + "\"" + entry.getValue().replace("\"", "\\\"") + "\",";
        }
        json = StringUtils.substringBeforeLast(json, ",");
        json += "}";
        return json;
    }
    /**
     * <p>Method converting {@link Map} into {@link T} form</p>
     */
    public static <T> T formMapToForm(Map<String,Object> map, Class<T> formClass) {
        return om.convertValue(map, formClass);
    }

}
