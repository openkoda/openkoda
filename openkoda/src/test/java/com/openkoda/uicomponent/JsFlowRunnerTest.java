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

package com.openkoda.uicomponent;

import com.openkoda.AbstractTest;
import com.openkoda.core.flow.PageModelMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class JsFlowRunnerTest  extends AbstractTest {

    @Autowired
    JsFlowRunner jsFlowRunner;

    String flow = "flow.then(a => a.model.put(\"users\", a.services.data.getRepository(\"user\").findAll()))";

    @Test
    public void testRunPreviewFlow() {
        PageModelMap result = jsFlowRunner.runPreviewFlow(flow, new HashMap<>(), null, -1, null );
        System.out.println(result);
    }

    @Test
    public void testRunLiveFlow() {
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

        PageModelMap result = jsFlowRunner.runLiveFlow(flow, new HashMap<>(), null, -1, null);
        System.out.println(result);
    }
}