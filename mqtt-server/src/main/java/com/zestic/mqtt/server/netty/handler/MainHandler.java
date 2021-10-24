package com.zestic.mqtt.server.netty.handler;

import com.zestic.log.Log;
import com.zestic.mqtt.server.netty.utils.NettyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class MainHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private static final Log logger = Log.get();

    public MainHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
        // 消息解码器出现异常
        if (msg.decoderResult().isFailure()) {
            handleDecoderFailure(ctx, msg);
            return;
        }

        MqttMessageType messageType = msg.fixedHeader().messageType();
        try {
            //协议事件参考：https://juejin.im/post/5bcc4e1ce51d457a0e17c908#heading-55
            switch (messageType) {
                case CONNECT:
                    break;

                case SUBSCRIBE:
                    break;

                case UNSUBSCRIBE:
                    break;

                case PUBLISH:
                    break;

                case PUBREC:
                    break;

                case PUBCOMP:
                    break;

                case PUBREL:
                    break;

                case DISCONNECT:
                    break;

                case PUBACK:
                    break;

                case PINGREQ:
                    break;

                default:
                    logger.error("Unsupported messageType:{}", messageType);
                    break;
            }
        } catch (Throwable ex) {
            logger.error("MqttMainHandler handle message exception: " + ex.getCause(), ex);
            ctx.fireExceptionCaught(ex);
            ctx.close();
        }
    }

    /**
     * Handle decoding error
     *
     * @param ctx
     * @param msg
     */
    private void handleDecoderFailure(ChannelHandlerContext ctx, MqttMessage msg) {
        final Channel channel = ctx.channel();
        Throwable cause = msg.decoderResult().cause();

        if (cause instanceof MqttUnacceptableProtocolVersionException) {
            // 不支持的协议版本
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false), null);
            channel.writeAndFlush(connAckMessage);

        } else if (cause instanceof MqttIdentifierRejectedException) {
            // 不合格的clientId
            MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false), null);
            channel.writeAndFlush(connAckMessage);
        }

        channel.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warn("MqttMainHandler-channelInactive clientId={},userName={}", NettyUtils.clientId(ctx.channel()), NettyUtils.userName(ctx.channel()));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("MqttMainHandler-exceptionCaught clientId={},userName={}", NettyUtils.clientId(ctx.channel()), NettyUtils.userName(ctx.channel()), cause);
        ctx.close();
    }

    /**
     * Processing a heartbeat timeout
     * reference：https://github.com/moquette-io/moquette/blob/master/broker/src/main/java/io/moquette/broker/MoquetteIdleTimeoutHandler.java
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                logger.warn("MqttMainHandler-idleStateEvent clientId={},userName={}", NettyUtils.clientId(ctx.channel()), NettyUtils.userName(ctx.channel()));
                //fire a channelInactive to trigger publish of Will
                ctx.fireChannelInactive();
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
