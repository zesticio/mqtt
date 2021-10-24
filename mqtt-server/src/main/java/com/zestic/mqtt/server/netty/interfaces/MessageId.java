package com.zestic.mqtt.server.netty.interfaces;

public interface MessageId {

    /**
     * To get the message Id
     * @param clientId
     * @return
     */
    Integer getNextMessageId(String clientId);
}
