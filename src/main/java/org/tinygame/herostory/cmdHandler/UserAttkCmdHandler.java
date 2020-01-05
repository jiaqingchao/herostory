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
    private static int subtractHp = 10;

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if(ctx == null || cmd == null){
            return;
        }

        //获取被攻击者Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (attkUserId == null){
            return;
        }

        //获取被攻击者Id
        int targetUserId = cmd.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        BraoadCaster.broadCast(newResult);

        //获取被攻击者
        User targetUser = UserManager.getUserById(targetUserId);
        if(targetUser == null){
            return;
        }

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        //减去HP
        targetUser.subtractHp(subtractHp);

        //广播减血消息
        broadCastSubtractHp(targetUserId, subtractHp);

        if(targetUser.getCurrHp() <= 0){
            // 清除死亡用户
            UserManager.removeUserById(targetUserId);
            //广播死亡消息
            broadcastDie(targetUserId);
        }
    }


    /**
     * 广播减血消息
     * @param targetUserId
     * @param subtractHp
     */
    private static void broadCastSubtractHp(int targetUserId, int subtractHp) {
        if(targetUserId <= 0 || subtractHp <= 0){
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder hpResultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        hpResultBuilder.setTargetUserId(targetUserId);
        hpResultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newHpResult = hpResultBuilder.build();
        BraoadCaster.broadCast(newHpResult);
    }

    /**
     * 广播死亡消息
     * @param targetUserId
     */
    private void broadcastDie(int targetUserId) {
        if(targetUserId <= 0){
            return;
        }

        GameMsgProtocol.UserDieResult.Builder dieResultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        dieResultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newDieResult = dieResultBuilder.build();
        BraoadCaster.broadCast(newDieResult);
    }
}
