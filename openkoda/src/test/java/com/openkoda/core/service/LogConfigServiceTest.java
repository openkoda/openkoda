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

package com.openkoda.core.service;

import com.openkoda.AbstractTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

public class LogConfigServiceTest extends AbstractTest {

    @Inject
    LogConfigService logConfigService;

    @Test
    public void setDebugForClassNames() {
//        assertTrue(false);
        /*
        Test scenarios:
            1.  if A is on, an is on list
                then i dont turn it on again

            2.  if A is on, and is not on list
                then i turn it off

            3.  if A is off, and is on list
                then i turn it on

            4.  if A is off, and is not on list
                then i dont turn it on

            5. Check if method accepts and works fine for different types of collections
         */
    }
}