package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg == null || !(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        ByteBuf byteBuf = frame.content();

        byteBuf.readShort();//消息长度
        int msgCode = byteBuf.readShort();//消息编号

        //获取消息构建者
        Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
        if (msgBuilder == null) {
            LOGGER.error("无法识别的消息，msgCode = {}", msgCode);
            return;
        }

        //拿到消息体
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);

        //构建消息
        Message newMsg = msgBuilder.build();

        if (newMsg != null) {
            ctx.fireChannelRead(newMsg);
        }
    }
}
