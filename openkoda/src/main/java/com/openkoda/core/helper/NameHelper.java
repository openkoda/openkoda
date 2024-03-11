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

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.*;
import static com.openkoda.model.component.Form.TABLE_NAME_PREFIX;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/**
 * Manipulate class or method names
 */
@Component("namehelper")
public class NameHelper {

    private static final CamelCaseToUnderscoresNamingStrategy namingStrategy = new CamelCaseToUnderscoresNamingStrategy();
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
    public static String toDynamicTableName(String tableName){
         return tableName.startsWith(TABLE_NAME_PREFIX) ? tableName : TABLE_NAME_PREFIX + tableName;
    }

    public static String toEntityName(String tableName){
        return LOWER_UNDERSCORE.to(UPPER_CAMEL, tableName.replace(TABLE_NAME_PREFIX, ""));
    }

    public static String toEntityKey(String tableName){
        return uncapitalize(toEntityName(tableName));
    }

    public static String toFieldName(String columnName){
        return LOWER_UNDERSCORE.to(LOWER_CAMEL, columnName);
    }

    public static String toColumnName(String fieldName){
        return LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName);
    }

    public static String toRepositoryName(String tableName){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "SecureGenerated" + toEntityName(tableName) + "Repository");
    }

}
