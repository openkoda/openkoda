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

package com.openkoda.core.form;

import com.openkoda.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamNameDataBinderTest extends AbstractTest {

    //TODO: Find replacement for deprecated class
    @Test
    public void replaceFirstLevel() {
        assertEquals("dto[a]", ParamNameDataBinder.replaceFirstLevel("dto.a"));
        assertEquals("dto[123]", ParamNameDataBinder.replaceFirstLevel("dto.123"));
        assertEquals("dto[a].b", ParamNameDataBinder.replaceFirstLevel("dto.a.b"));
        assertEquals("dto[abc]", ParamNameDataBinder.replaceFirstLevel("dto.abc"));
    }

    @Test
    public void replaceAllLevels() {
        assertEquals("dto[a]", ParamNameDataBinder.replaceAllLevels("dto.a"));
        assertEquals("dto[123]", ParamNameDataBinder.replaceAllLevels("dto.123"));
        assertEquals("dto[a][b]", ParamNameDataBinder.replaceAllLevels("dto.a.b"));
        assertEquals("dto[abc]", ParamNameDataBinder.replaceAllLevels("dto.abc"));
    }
}