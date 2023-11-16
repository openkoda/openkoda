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

package com.openkoda.model.module;

import jakarta.validation.constraints.NotNull;

/**
 * <p>Interface to application extension modules</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public interface Module {

    /**
     * <p>getLabel.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @NotNull
    String getLabel();

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @NotNull
    String getName();

    /**
     * <p>getOrdinal.</p>
     *
     * @return a int.
     */
    int getOrdinal();

    default String getModuleView(String viewName) {
        return String.format("module/%s/%s", getName(), viewName);
    }

    @NotNull
    default String getMainViewName() {
        return "main";
    }


    static Module empty = new Module() {
        @Override
        public String getLabel() {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getOrdinal() {
            return 0;
        }
    };

}
