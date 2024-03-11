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

import com.openkoda.model.User;
import com.openkoda.model.file.File;
import com.openkoda.model.task.Email;
import com.openkoda.uicomponent.annotation.Autocomplete;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MessagesServices {

    @Autocomplete(doc="Send an email message")
    Email sendEmail(String email, String subject, String message, List<File> attachments);
    
    @Autocomplete(doc="Send a scheduled email message")
    Email sendEmail(String email, String subject, String message, List<File> attachments, LocalDateTime sendOn);
    
    @Autocomplete(doc="Send an email message based on a template")
    Email sendEmail(String email, String subject, String resourceName, Map<String, Object> model, List<File> attachments);
    
    @Autocomplete(doc="Send a scheduled email message based on a template")
    Email sendEmail(String email, String subject, String resourceName, Map<String, Object> model, List<File> attachments, LocalDateTime sendOn);
    
    @Autocomplete(doc="Send message to a specific user through WebSocket")
    boolean sendToWebsocketUser(User user, String channelName, Object payload);
    @Autocomplete(doc="Send message to a specific WebSocket channel")
    boolean sendToWebsocketChannel(String channelName, Object payload);

}
