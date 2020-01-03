package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.BraoadCaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 自定义消息处理器
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        //从指令对象获取用户id和英雄形象
        GameMsgProtocol.UserEntryCmd cmd = msg;
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(cmd.getUserId());
        resultBuilder.setHeroAvatar(cmd.getHeroAvatar());

        //将用户加入字典
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setHeroAvatar(heroAvatar);
        UserManager.addUser(newUser);

        //将userId 附着到Channel
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        //构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        BraoadCaster.broadCast(newResult);
    }
}
