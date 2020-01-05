package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if(ctx == null || cmd == null){
            return;
        }

        //从指令对象获取用户id和英雄形象
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User currUser : UserManager.listUser()) {
            if (currUser == null) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.getUserId());
            userInfoBuilder.setHeroAvatar(currUser.getHeroAvatar());

            //获取移动状态
            MoveState moveState = currUser.getMoveState();
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder
                    moveStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            moveStateBuilder.setFromPosX(moveState.getFromPosX());
            moveStateBuilder.setFromPosY(moveState.getFromPosY());
            moveStateBuilder.setToPosX(moveState.getToPosX());
            moveStateBuilder.setToPosY(moveState.getToPosY());
            moveStateBuilder.setStartTime(moveState.getStartTime());
            //将移动状态设置到用户消息
            userInfoBuilder.setMoveState(moveStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.channel().writeAndFlush(newResult);
    }
}
