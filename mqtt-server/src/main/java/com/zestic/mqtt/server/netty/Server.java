package com.zestic.mqtt.server.netty;

import com.zestic.log.Log;
import com.zestic.mqtt.server.config.Configuration;
import com.zestic.mqtt.server.netty.handler.MainHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;

public class Server extends Endpoint {

    private static final Log logger = Log.get();

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel tcpChannel;
    private Channel tcpSslChannel;
    private Channel webSocketChannel;
    private Channel webSocketSslChannel;
    private Configuration configuration;

    /**
     * @param configuration
     */
    public Server(final Configuration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    public void initialize() {
        // get runtime processors for thread-size
        int cores = Runtime.getRuntime().availableProcessors();

        //initialize boss group and worker group
        if (configuration.getEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(2 * cores);
            this.workerGroup = new EpollEventLoopGroup(10 * cores);
        } else {
            this.bossGroup = new NioEventLoopGroup(2 * cores);
            this.workerGroup = new NioEventLoopGroup(10 * cores);
        }
        // Create ServerBootstrap
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(this.configuration.getEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new NettyInitializer(this.configuration, new MainHandler()))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_RCVBUF, 65536)
                .childOption(ChannelOption.SO_SNDBUF, 65536)
                .childOption(ChannelOption.SO_BACKLOG, 1024);

        // Check for extra epoll-options
        if (this.configuration.getEpoll()) {
            bootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED)
                    .option(EpollChannelOption.TCP_FASTOPEN, 3)
                    .option(EpollChannelOption.SO_REUSEPORT, true);
        }
    }

    public void start(){
        {
            tcpChannel = start(Constants.ServerProtocolType.TCP, this.configuration.getTcpPort(), false);
        }
        {
            if(this.configuration.getSslEnabled()) {
                tcpSslChannel = start(Constants.ServerProtocolType.TCP, this.configuration.getTcpSslPort(), true);
            }
        }
        {
            if(this.configuration.getWebSocketEnable()) {
                webSocketChannel = start(Constants.ServerProtocolType.WEB_SOCKET, this.configuration.getWebSocketPort(), false);
            }
        }
        {
            if(this.configuration.getWebSocketSsl()) {
                webSocketSslChannel = start(Constants.ServerProtocolType.WEB_SOCKET, this.configuration.getWebSocketSslPort(), true);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop()));
    }

    private Channel start(Constants.ServerProtocolType protocolType, Integer port, Boolean ssl) {
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        if (!StringUtil.isNullOrEmpty(this.configuration.getHostname())) {
            socketAddress = new InetSocketAddress(this.configuration.getHostname(), port);
        }
        ChannelFuture channelFuture = bootstrap.bind(socketAddress).addListener((future) -> {
            if (future.isSuccess()) {
                logger.info(protocolType.name + " server started at port={} useSsl={}", port, ssl);
            } else {
                logger.error(protocolType.name + " server start failed at port={} useSsl={} errMsg={}", port, ssl, future.cause().getMessage());
            }
        });
        return channelFuture.channel();
    }

    public void stop() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();

        if (null != tcpChannel) {
            logger.info("Close tcp channel.");
            tcpChannel.closeFuture().syncUninterruptibly();
        }

        if (null != tcpSslChannel) {
            logger.info("Close tcp ssl channel.");
            tcpSslChannel.closeFuture().syncUninterruptibly();
        }

        if (null != webSocketChannel) {
            logger.info("Close web socket channel.");
            webSocketChannel.closeFuture().syncUninterruptibly();
        }

        if (null != webSocketSslChannel) {
            logger.info("Close web socket ssl channel.");
            webSocketSslChannel.closeFuture().syncUninterruptibly();
        }
        logger.info("Server stopped.");
    }

    /**
     * returns type of connection
     *
     * @return
     */
    @Override
    public Type getType() {
        return Type.SERVER;
    }
}
