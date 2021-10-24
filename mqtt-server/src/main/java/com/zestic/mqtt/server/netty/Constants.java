package com.zestic.mqtt.server.netty;

public class Constants {

    public enum ServerProtocolType {
        TCP("tcp"),
        WEB_SOCKET("webSocket");

        public final String name;

        ServerProtocolType(String name) {
            this.name = name;
        }
    }

    /**
     *
     */
    public static final String HANDLER_IDLE_STATE = "idleStateHandler";
    public static final String MQTT_SUB_PROTOCOL_CSV_LIST = "mqtt, mqttv3.1, mqttv3.1.1";
    public static final String HANDLER_MQTT_DECODER = "mqttDecoderHandler";
    public static final String HANDLER_MQTT_ENCODER = "mqttEncoderHandler";
    public static final String HANDLER_MQTT_MAIN = "mqttMainHandler";
}
