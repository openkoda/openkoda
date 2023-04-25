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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.service.FrontendResourceService;
import com.openkoda.model.FrontendResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrontendResourceServiceTest extends AbstractTest {

    private static final String FRONTEND_RESOURCE_NAME = "New Frontend Resource Entry";


    @Autowired
    private FrontendResourceService frontendResourceService;

    @Test
    public void validateContent() throws Exception {
        assertTrue(frontendResourceService.validateContent("Add content here 234234 <a href=\"http://localhost:8080/html/organization\"> go </a>", null, "dto.content"));
        assertTrue(frontendResourceService.validateContent("Add content here 234234 <a href=\"http://onet.pl/html/organization\"> go </a>", null, "dto.content"));
        assertTrue(frontendResourceService.validateContent("<div><a></a></div>", null, "dto.content"));
        assertTrue(frontendResourceService.validateContent("hej ehej <div><a href=\"http://onet.pl\">asas</a></div> huhu", null, "dto.content"));
        assertTrue(frontendResourceService.validateContent("hej ehej huhu", null, "dto.content"));
    }

    @Test
    public void checkNameExistsTrue() {
//        given
        FrontendResource frontendResource = mock(FrontendResource.class);
        BindingResult br = mock(BindingResult.class);

//        when
        when(frontendResourceRepository.findByName(anyString())).thenReturn(frontendResource);
        boolean result = frontendResourceService.checkNameExists(FRONTEND_RESOURCE_NAME, br);

//        then
        assertTrue(result);
    }

    @Test
    public void checkNameExistsFalse() {
//        given
        BindingResult br = mock(BindingResult.class);

//        when
        when(frontendResourceRepository.findByName(anyString())).thenReturn(null);
        boolean result = frontendResourceService.checkNameExists(FRONTEND_RESOURCE_NAME, br);

//        then
        assertFalse(result);
    }

}