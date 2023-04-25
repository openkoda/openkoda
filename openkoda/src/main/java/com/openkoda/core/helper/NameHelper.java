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

package com.openkoda.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manipulate class or method names
 */
@Component("namehelper")
public class NameHelper {

    public final static String DELIMITER = "#;#";

    /**
     * Creates method description
     */
    public static String createMethodDescription(Method method) {
        if (method == null) { return ""; }

        String signature = method.getDeclaringClass().getSimpleName()
                + " :: "
                + method.getName()
                + "("
                + Arrays.stream(method.getGenericParameterTypes()).map(pt -> getClassName(pt.getTypeName())).collect
                (Collectors.joining(", "))
                + ")";
        return signature;
    }

    /**
     * Gets substring from input {@link String} after last '.' in it.
     */
    public static String getClassName(String eventObjectType) {
        return StringUtils.substringAfterLast(eventObjectType, ".");
    }

    /**Creates array of Class objects from String array of class names
     * @param classNames
     * @return
     */
    public static Class<?>[] getClasses(String[] classNames){
        try {
            if(classNames != null){
                List<Class<?>> classes = new ArrayList<>();
                for(String cs : classNames){
                    classes.add(Class.forName(cs));
                }
                return classes.toArray(new Class[0]);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (Class<?>[]) new Class[0];
    }

}
