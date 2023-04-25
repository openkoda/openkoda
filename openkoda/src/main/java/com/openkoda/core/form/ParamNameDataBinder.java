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

package com.openkoda.core.form;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

@Deprecated
public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {

    public ParamNameDataBinder(Object target, String objectName, MutablePropertyValues pv) {
        super(target, objectName);
        for (PropertyValue a : pv.getPropertyValues()) {
            if (a.getName().startsWith("dto.")) {
                String newName = replaceFirstLevel(a.getName());
                pv.addPropertyValue(newName, pv.get(a.getName()));
            }
        }
    }

    public static String replaceFirstLevel(String name) {
        String nameWithoutDtoPrefix = name.substring(4);
        int closingPos = StringUtils.indexOfAny(nameWithoutDtoPrefix, ".[(");
        String result;
        if (closingPos < 0) {
            result = "dto[" + nameWithoutDtoPrefix + "]";
        } else {
            result = "dto[" + StringUtils.substring(nameWithoutDtoPrefix, 0, closingPos)
                    + "]" + StringUtils.substring(nameWithoutDtoPrefix, closingPos);
        }
        return result;
    }
    public static String replaceAllLevels(String name) {
        String nameWithoutDtoPrefix = name.substring(4);
        String result = nameWithoutDtoPrefix.replaceAll("\\.", "][");
        result = "dto[" + result + "]";
        return result;
    }

}