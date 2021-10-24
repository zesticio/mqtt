package com.zestic.mqtt.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${mqtt.server.epoll}")
    private Boolean epoll = Boolean.FALSE;

    @Value("${mqtt.server.port}")
    private Integer tcpPort = 1883;
    @Value("${mqtt.server.ssl}")
    private Boolean sslEnabled = Boolean.FALSE;
    @Value("${mqtt.server.ssl.port}")
    private Integer tcpSslPort = 8883;

    @Value("${mqtt.server.web-socket}")
    private Boolean webSocketEnable = Boolean.FALSE;
    @Value("${mqtt.server.web-socket.port}")
    private Integer webSocketPort = 1884;
    @Value("${mqtt.server.web-socket.ssl}")
    private Boolean webSocketSsl = Boolean.FALSE;
    @Value("${mqtt.server.web-socket.ssl.port}")
    private Integer webSocketSslPort = 8884;
    @Value("${mqtt.server.web-socket.ssl.port}")
    private Boolean enableClientCa = Boolean.FALSE;

    @Value("${mqtt.server.hostname}")
    private String hostname = "zestic.mqtt.io";

    @Value("${mqtt.netty.channel-timeout-seconds}")
    private Integer channelTimeoutSeconds = 200;

    @Value("${mqtt.server.context}")
    private String context = "/mqtt.io";

    @Value("${mqtt.server.ssl.key-file-path}")
    private String sslKeyFilePath;
    @Value("${mqtt.server.ssl.key-store-type}")
    private String sslKeyStoreType;
    @Value("${mqtt.server.ssl.manager-password}")
    private String sslManagerPassword;
    @Value("${mqtt.server.ssl.store-password}")
    private String sslStorePassword;

}
