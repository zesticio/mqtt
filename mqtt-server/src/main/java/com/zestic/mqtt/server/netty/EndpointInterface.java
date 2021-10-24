package com.zestic.mqtt.server.netty;

public interface EndpointInterface {

    /**
     * @return Returns the type // server or client
     */
    Endpoint.Type getType();

    enum Type { CLIENT, SERVER }
}
