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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReflectionHelper {

    @Autowired
    private MethodParameterHelper methodParameterHelper;

    public String getNameWithParamNames(Method method){
        return method.getName() + "(" + methodParameterHelper.getParameterNames(method) + ")";
    }

    public String getNameWithParamNamesAndTypes(Method method){
        return  method.getName() + "(" + methodParameterHelper.getParameterNamesAndTypes(method) + ")";
    }
    public String getNameWithParamNamesAndTypesAndReturnType(Method method, String methodPrefix){
        return getShortName(method.getGenericReturnType()) + " " + methodPrefix + method.getName() + "(" + methodParameterHelper.getParameterNamesAndTypes(method) + ")";
    }

    /**
     * Removes package names from type name, e.g changes java.util.List<java.lang.String> to List<String>.
     * It assumes simple type form with only one level of "<>", that is List<String> is acceptable but List<List<String>> is not
     * @param type
     * @return
     */
    public String getShortName(Type type){
        String result="";
        if(type instanceof Class<?>){
            Class<?> cType = (Class<?>) type;
            return cType.getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            List<String> replaces = new ArrayList<>();
            replaces.add(((Class<?>) pType.getRawType()).getPackageName());
            for(Type typeArgument : pType.getActualTypeArguments()){
                if(typeArgument instanceof Class<?>) {
                    replaces.add(((Class<?>) typeArgument).getPackageName());
                }
            }
            result = type.getTypeName();
            for(String rep : replaces){
                result = result.replace(rep + ".", "");
            }
        }
        return result;
    }
    /**
     * returns true if type is primitive, wrapper of primitive, Enum or String
     * @return
     */
    public boolean isSimpleType(Field field){
        return ClassUtils.isPrimitiveOrWrapper(field.getType()) || field.getType().isAssignableFrom(String.class) || field.getType().isEnum();
    }
    /**
     * returns true if type is boolean or Boolean
     * @param field
     * @return
     */
    public boolean isBoolean(Field field){
        return field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class);
    }

    public Method[] getDeclaredMethods(String className){
        try {
            return Class.forName(className).getDeclaredMethods();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
