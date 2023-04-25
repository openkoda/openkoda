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

package com.openkoda;

import com.openkoda.core.form.ParamNameDataBinderTest;
import com.openkoda.core.helper.RuleSpelHelperTests;
import com.openkoda.core.helper.UrlHelperTest;
import com.openkoda.core.service.LogConfigServiceTest;
import com.openkoda.core.service.backup.BackupWriterTest;
import com.openkoda.service.*;
import com.openkoda.service.map.MapServiceTest;
import com.openkoda.uicomponent.JsFlowRunnerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-31
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MapServiceTest.class,
        BackupServiceTest.class,
        EventListenerServiceTest.class,
        FrontendResourceServiceTest.class,
        PushNotificationServiceTest.class,
        RoleServiceTest.class,
        SchedulerServiceTests.class,
        UserServiceTest.class,
        JsFlowRunnerTest.class,
        ParamNameDataBinderTest.class,
        RuleSpelHelperTests.class,
        UrlHelperTest.class,
        BackupWriterTest.class,
        LogConfigServiceTest.class
})
public class OpenkodaTestSuite {
}
