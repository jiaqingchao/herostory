package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.BraoadCaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);
    private int hurt = 10;

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if(ctx == null || cmd == null){
            return;
        }

        //获取被攻击者Id
        Integer attlUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attlUserId == null){
            return;
        }

        //获取被攻击者Id
        int targetUserId = cmd.getTargetUserId();

        //获取被攻击者
        User targetUser = UserManager.getUserById(targetUserId);
        if(targetUser == null){
            return;
        }
        targetUser.setSurplusHp(targetUser.getSurplusHp() - hurt);

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attlUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        BraoadCaster.broadCast(newResult);

        //减去HP
        GameMsgProtocol.UserSubtractHpResult.Builder hpResultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        hpResultBuilder.setTargetUserId(targetUserId);
        hpResultBuilder.setSubtractHp(hurt);

        GameMsgProtocol.UserSubtractHpResult newHpResult = hpResultBuilder.build();
        BraoadCaster.broadCast(newHpResult);

        if(targetUser.getSurplusHp() > 0){
            return;
        }

        UserManager.removeUserById(targetUserId);

        GameMsgProtocol.UserDieResult.Builder dieResultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        dieResultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newDieResult = dieResultBuilder.build();
        BraoadCaster.broadCast(newDieResult);

    }
}
