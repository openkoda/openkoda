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

package com.openkoda.core.service;

import com.openkoda.core.configuration.WebSocketConfig;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.User;
import com.openkoda.repository.user.UserRepository;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Sends message to Websocket channel.
 * Channel name is a logical name for a messaging queue
 * There are (for now) two types of channels:
 * - broadcast channel
 * - user channel
 *
 * For broadcast channel:
 * - on client side subscribe to channel eg. '/queue/broadcast-channel'
 * - on server/sender side, use sendToChannel method with channelName == '/queue/broadcast-channel'
 *
 * For user channel:
 * - on client side subscribe to channel eg. '/user/queue/user-notifications'
 * - on server/sender side, use sendToUserChannel with channelName == '/queue/user-notifications'
 */
@Service
public class WebsocketService implements ReadableCode, LoggingComponentWithRequestId {

    @Inject
    private SimpMessagingTemplate messagingTemplate;

    @Inject
    private UserRepository userRepository;

    private void checkChannelName(String channelName) {
        if (not(StringUtils.startsWith(channelName, WebSocketConfig.CHANNEL_PREFIX))) {
            warn("[WebsocketService] Probably wrong broadcast channel name: {}", channelName);
        }
    }

    public boolean sendToChannel(String channelName, Object payload) {
        checkChannelName(channelName);
        messagingTemplate.convertAndSend(channelName, payload);
        return true;
    }

    public boolean sendToChannel(String channelName, Object payload, Map<String, Object> headers) {
        checkChannelName(channelName);
        messagingTemplate.convertAndSend(channelName, payload, headers);
        return true;
    }

    public boolean sendToUserChannel(String userEmail, String channelName, Object payload) {
        checkChannelName(channelName);
        messagingTemplate.convertAndSendToUser(userEmail, channelName, payload);
        return true;
    }

    public boolean sendToUserChannel(Long userId, String channelName, Object payload) {
        checkChannelName(channelName);
        String userEmail = userRepository.findUserEmailByUserId(userId);
        sendToUserChannel(userEmail, channelName, payload);
        return true;
    }

    public boolean sendToUserChannel(User user, String channelName, Object payload) {
        checkChannelName(channelName);
        sendToUserChannel(user.getEmail(), channelName, payload);
        return true;
    }

    public boolean sendToUserChannel(String userEmail, String channelName, Object payload, Map<String, Object> headers) {
        checkChannelName(channelName);
        messagingTemplate.convertAndSendToUser(userEmail, channelName, payload, headers);
        return true;
    }

    public boolean sendToUserChannel(Long userId, String channelName, Object payload, Map<String, Object> headers) {
        checkChannelName(channelName);
        String userEmail = userRepository.findUserEmailByUserId(userId);
        sendToUserChannel(userEmail, channelName, payload, headers);
        return true;
    }

    public boolean sendToUserChannel(User user, String channelName, Object payload, Map<String, Object> headers) {
        checkChannelName(channelName);
        sendToUserChannel(user.getEmail(), channelName, payload, headers);
        return true;
    }

}
