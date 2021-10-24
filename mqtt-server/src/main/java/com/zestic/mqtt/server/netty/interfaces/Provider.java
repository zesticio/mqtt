package com.zestic.mqtt.server.netty.interfaces;

import io.netty.handler.ssl.SslContext;

public interface Provider {

    /**
     * Initializes the SSL Context
     * <p>
     * It is recommended to use：keytool -genkey -alias <desired certificate alias>
     * -keystore <path to keystore.pfx>
     * -storetype PKCS12
     * -keyalg RSA
     * -storepass <password>
     * -validity 730
     * -keysize 2048
     *
     * @param enableClientCa
     * @return
     * @throws Exception
     */
    SslContext initializeSslContext(boolean enableClientCa) throws Exception;

    /**
     * To get the message Id storage implementation
     *
     * @return
     */
    MessageId initializeMessageId();

    /**
     * Access to the session storage implementation
     *
     * @return
     */
    Session initializeSession();

    /**
     * For topic subscription storage implementation
     *
     * @param sessionStore
     * @return
     */
    Subscription initializeSubscription(Session sessionStore);

    /**
     * Access to the pub Message Message store
     *
     * @return
     */
    Publish initializePublishMessage();

    /**
     * 获取节点名称
     *
     * @return
     */
    String getNodeName();
}
