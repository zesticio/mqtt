package com.zestic.mqtt.server.netty.interfaces;

import com.zestic.mqtt.server.netty.PublishMessage;

import java.util.List;

/**
 * The PUBLISH resend message storage, when QoS = 1 and Qo exist the retransmission mechanism when S = 2
 */
public interface Publish extends ControlPacket {
    /**
     * Store messages
     *
     * @param message
     */
    void add(PublishMessage message);

    /**
     * To get the message
     *
     * @param clientId
     * @return
     */
    List<PublishMessage> get(String clientId);

    /**
     * To get the message
     *
     * @param clientId
     * @param messageId
     * @return
     */
    PublishMessage get(String clientId, String messageId);

    /**
     * Delete the specified message
     *
     * @param clientId
     * @param messageId
     */
    void remove(String clientId, int messageId);

    /**
     * Remove all user message
     *
     * @param clientId
     */
    void removeAll(String clientId);
}
