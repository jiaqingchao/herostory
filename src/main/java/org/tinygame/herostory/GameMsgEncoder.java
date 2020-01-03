package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg == null || !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        int msgCode = GameMsgRecognizer.getMsgCodeByMsg(msg.getClass());

        if (msgCode <= -1) {
            LOGGER.error("无法识别的消息， msgClazz = " + msg.getClass().getName());
            return;
        }

        GeneratedMessageV3 result = (GeneratedMessageV3) msg;
        byte[] byteArray = result.toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort(0);
        byteBuf.writeShort(msgCode);
        byteBuf.writeBytes(byteArray);

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);

        super.write(ctx, frame, promise);

    }
}
