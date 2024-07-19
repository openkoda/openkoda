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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Helper to simplify accessing messages in code.
 * This finds messages automatically found from src/main/resources (files named messages_*.properties)
 * This class uses hard-coded English locale.
 */
@Component("messages")
public class Messages {

    private static final String NO_MESSAGE = "|NO_MESSAGE|";
    @Inject
    private MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @Value("${show.message.key.for.default.field.label:false}")
    private boolean showMessageKeyForDefaultFieldLabel;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource);
    }

    public String get(String code, String ... args) {
        return accessor.getMessage(code, args, "");
    }


    /**
     * Method returning message field label if exists
     * or generate default one otherwise
     */
    public String getFieldLabel(String code, String fieldName) {
        String result = accessor.getMessage(code, NO_MESSAGE);
        if (NO_MESSAGE.equals(result)) {
            result = getDefaultLabel(fieldName);
            if (showMessageKeyForDefaultFieldLabel) {
                result += " (" + code + ")";
            }
        }
        return result;
    }

    @NotNull
    public String getDefaultLabel(String fieldName) {
        String result = WordUtils.capitalize(Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(fieldName))).filter(s -> !s.equals(".")).collect(Collectors.joining(" ")));
        return result;
    }


    /**
     * Method returning generic table header label if exists
     * or generate default one otherwise
     */
    public String getTableHeaderLabel(String code, String fieldName) {
        String result = accessor.getMessage(code, NO_MESSAGE);
        if (NO_MESSAGE.equals(result)) {
            if(fieldName.contains(".")) {
//                modify header label if it refers to another entity and contains repeats
                String[] fieldNameParts = fieldName.split("\\.");
                if(fieldNameParts[1].contains(fieldNameParts[0])) {
                    fieldName = fieldNameParts[1];
                }
            }
            result = WordUtils.capitalize(Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(fieldName))).filter(s -> !s.equals(".")).collect(Collectors.joining(" ")));
            if (showMessageKeyForDefaultFieldLabel) {
                result += " (" + code + ")";
            }
        }
        return result;
    }
    /**
     * Method returning message field placeholder if exists
     * or generate default one otherwise
     */
    public String getFieldPlaceholder(String code, String fieldName) {
        String result = accessor.getMessage(code, NO_MESSAGE);
        if (NO_MESSAGE.equals(result)) {
            result = getFieldLabel(code, fieldName) + "...";
        }
        return result;
    }
    /**
     * Method returning message field tooltip if exists
     * or generate default one otherwise
     */
    public String getFieldTooltip(String code, String fieldName) {
        String result = accessor.getMessage(code, NO_MESSAGE);
        if (NO_MESSAGE.equals(result)) {
            result = StringUtils.EMPTY;
        }
        return result;
    }

    /**
     * Method returning message column name if exists
     * or generate default one otherwise
     */
    public String getColumnLabel(String columnName) {
        String result = accessor.getMessage(columnName, NO_MESSAGE);
        if (NO_MESSAGE.equals(result)) {
            result = WordUtils.capitalize(String.join(" ", columnName.split("_")));
            if (showMessageKeyForDefaultFieldLabel) {
                result += " (" + columnName + ")";
            }
        }
        return result;
    }
}
