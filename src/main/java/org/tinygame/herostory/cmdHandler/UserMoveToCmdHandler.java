package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.BraoadCaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if(ctx == null || cmd == null){
            return;
        }

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (userId == null) {
            return;
        }

        //获取移动中的用户
        User moveUser = UserManager.getUserById(userId);
        if(moveUser == null){
            return;
        }

        MoveState moveState = moveUser.getMoveState();
        //设置位置和时间
        moveState.setFromPosX(cmd.getMoveFromPosX());
        moveState.setFromPosY(cmd.getMoveFromPosY());
        moveState.setToPosX(cmd.getMoveToPosX());
        moveState.setToPosY(cmd.getMoveToPosY());
        moveState.setStartTime(System.currentTimeMillis());

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(moveState.getFromPosX());
        resultBuilder.setMoveFromPosY(moveState.getFromPosY());
        resultBuilder.setMoveToPosX(moveState.getToPosX());
        resultBuilder.setMoveToPosY(moveState.getToPosY());
        resultBuilder.setMoveStartTime(moveState.getStartTime());

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        BraoadCaster.broadCast(newResult);
    }
}
