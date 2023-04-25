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

import com.openkoda.AbstractTest;
import com.openkoda.core.multitenancy.TenantResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.openkoda.controller.common.URLConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlHelperTest extends AbstractTest {

    UrlHelper urlHelper = new UrlHelper();

    HttpServletRequest request;

    @BeforeEach
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getServletPath()).thenReturn("/html/organization/123/shoe/567/action");
        Mockito.when(request.getLocalAddr()).thenReturn("localhost");
        Mockito.when(request.getMethod()).thenReturn("post");
    }

    @Test
    public void getTenantedResourceWhenBoth() {
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        assertEquals(Long.valueOf(123L), t.organizationId);
        assertEquals("shoe", t.entityKey);
    }

    @Test
    public void getTenantedResourceWhenOrgId() {
        Mockito.when(request.getServletPath()).thenReturn("/html/organization/123/");
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        assertEquals(Long.valueOf(123L), t.organizationId);
        Assertions.assertNull(t.entityKey);
    }

    @Test
    public void getTenantedResourceWhenEntityKey() {
        Mockito.when(request.getServletPath()).thenReturn("/html/shoe/123/action");
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        Assertions.assertNull(t.organizationId);
        assertEquals("shoe", t.entityKey);
    }

    @Test
    public void getTenantedResource1() {
        Mockito.when(request.getServletPath()).thenReturn("/html/organization/123/shoe/new/setting");
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        assertEquals(Long.valueOf(123L), t.organizationId);
        assertEquals("shoe", t.entityKey);
    }

    @Test
    public void getTenantedResource2() {
        Mockito.when(request.getServletPath()).thenReturn("/html/organization/123/organization/setting");
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        assertEquals(Long.valueOf(123L), t.organizationId);
        assertEquals("organization", t.entityKey);
    }

    @Test
    public void getTenantedResource3() {
        Mockito.when(request.getServletPath()).thenReturn(_HTML_ORGANIZATION + _NEW + _ORGANIZATION + _SETTINGS);
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        Assertions.assertNull(t.organizationId);
        assertEquals("organization", t.entityKey);
    }

    @Test
    public void getTenantedResource4() {
        Mockito.when(request.getServletPath()).thenReturn(_HTML_ORGANIZATION + "/232" + _ORGANIZATION + _SETTINGS);
        TenantResolver.TenantedResource t = urlHelper.getTenantedResource(request);
        assertEquals(Long.valueOf(232L), t.organizationId);
        assertEquals("organization", t.entityKey);
    }

}