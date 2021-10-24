package com.zestic.mqtt.server.netty.interfaces;

import com.zestic.mqtt.server.netty.ClientSession;

public interface Session extends ControlPacket {

    /**
     * Store session
     */
    void add(ClientSession clientSession);

    /**
     * Get session
     */
    ClientSession get(String clientId);

    /**
     * Remove session
     */
    void remove(String clientId);

    /**
     * The current connection number of the session
     *
     * @return
     */
    int count();
}
