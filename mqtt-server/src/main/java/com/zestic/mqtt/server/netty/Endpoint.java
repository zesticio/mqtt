package com.zestic.mqtt.server.netty;

import com.zestic.mqtt.server.config.Configuration;

public abstract class Endpoint implements EndpointInterface {

    private final Configuration configuration;

    /**
     * @param configuration
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public Endpoint(final Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration(){
        return configuration;
    }
}
