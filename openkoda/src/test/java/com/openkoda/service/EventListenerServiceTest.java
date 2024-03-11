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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.service.event.EventConsumer;
import com.openkoda.core.service.event.EventListenerService;
import com.openkoda.model.component.event.EventListenerEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-11
 */
public class EventListenerServiceTest extends AbstractTest {

    @Autowired
    private EventListenerService eventListenerService;

    @Test
    @Disabled
    @WithMockUser(roles = "TEST")
    public void createNewRoleByTypeAnyTestException() {
//        given
        EventListenerEntry listenerEntry = new EventListenerEntry("com.openkoda.core.service.event.ApplicationEvent",
                "USER_CREATED", "com.openkoda.model.User", "com.openkoda.core.service.event.EventListenerService", "printSth");
        listenerEntry.setId(123L);

//        when
        doCallRealMethod().when(applicationEventService).registerEventListener(any(), any(EventConsumer.class), any(), any(), any(), any(), any());
        when(eventListenerRepository.findOne(123L)).thenReturn(listenerEntry);
        eventListenerService.loadFromDb(listenerEntry.getId());

//        then
//        exception java.lang.NoSuchMethodException is thrown and caught in a method

    }
}
