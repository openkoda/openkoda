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
