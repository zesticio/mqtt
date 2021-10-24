package com.zestic.mqtt.server.netty;

import com.zestic.core.io.resource.ResourceUtil;
import com.zestic.mqtt.server.config.Configuration;
import com.zestic.mqtt.server.netty.codec.WebSocketCodec;
import com.zestic.mqtt.server.netty.handler.MainHandler;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

public class NettyInitializer extends ChannelInitializer<Channel> {

    private final Configuration configuration;
    private final MainHandler handler;

    public NettyInitializer(Configuration configuration, MainHandler handler) {
        this.configuration = configuration;
        this.handler = handler;
    }

    @Override
    public void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addFirst(Constants.HANDLER_IDLE_STATE,
                new IdleStateHandler(0,
                        0,
                        configuration.getChannelTimeoutSeconds()));
        if (configuration.getSslEnabled()) {
            pipeline.addLast("ssl",
                    buildSslHandler(channel.alloc(),
                            build(this.configuration.getEnableClientCa(),
                                    this.configuration.getSslKeyFilePath(),
                                    this.configuration.getSslKeyStoreType(),
                                    this.configuration.getSslManagerPassword(),
                                    this.configuration.getSslStorePassword()),
                            configuration.getEnableClientCa()));
        }

        //webSocket协议
        if (this.configuration.getWebSocketEnable()) {
            // The request and reply messages for HTTP message encoding or decoding
            pipeline.addLast("http-codec", new HttpServerCodec());
            // Synthesize the HTTP message parts a complete HTTP message
            pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
            // The HTTP message is compressed coding
            pipeline.addLast("compressor ", new HttpContentCompressor());
            pipeline.addLast("protocol",
                    new WebSocketServerProtocolHandler(this.configuration.getContext(),
                            Constants.MQTT_SUB_PROTOCOL_CSV_LIST,
                            true,
                            65536));
            pipeline.addLast("mqttWebSocketCodec", new WebSocketCodec());
        }

        //mqtt解码编码
        pipeline.addLast(Constants.HANDLER_MQTT_DECODER, new MqttDecoder());
        pipeline.addLast(Constants.HANDLER_MQTT_ENCODER, MqttEncoder.INSTANCE);

        //mqtt操作handler
        pipeline.addLast(Constants.HANDLER_MQTT_MAIN, handler);
    }

    private ChannelHandler buildSslHandler(ByteBufAllocator alloc, SslContext sslContext, boolean needClientAuth) {
        SSLEngine sslEngine = sslContext.newEngine(alloc);
        //The server mode
        sslEngine.setUseClientMode(false);
        //Whether the client
        if (needClientAuth) {
            sslEngine.setNeedClientAuth(true);
        }
        return new SslHandler(sslEngine);
    }

    private SslContext build(boolean enableClientCa, String sslKeyFilePath, String sslKeyStoreType, String sslManagerPwd, String sslStorePwd) throws Exception {
        try (InputStream ksInputStream = ResourceUtil.getStream(sslKeyFilePath)) {
            KeyStore ks = KeyStore.getInstance(sslKeyStoreType);
            ks.load(ksInputStream, sslStorePwd.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, sslManagerPwd.toCharArray());
            SslContextBuilder contextBuilder = SslContextBuilder.forServer(kmf);

            if (enableClientCa) {
                contextBuilder.clientAuth(ClientAuth.REQUIRE);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
                contextBuilder.trustManager(tmf);
            }

            return contextBuilder.build();
        }
    }
}
