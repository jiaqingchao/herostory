package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息代码和消息体字典
     */
    private static final Map<Integer, GeneratedMessageV3> msgCodeAndMsgBodyMap = new HashMap<>();

    /**
     * 消息代码和消息体字典
     */
    private static final Map<Class<?>, Integer> msgBodyAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化默认构造器
     */
    private GameMsgRecognizer() {
    }

    public static void init() {
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class innerClazz : innerClazzArray) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }

            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }
                try {
                    Object resultObj = innerClazz.getDeclaredMethod("getDefaultInstance").
                            invoke(innerClazz);

                    LOGGER.info("{} <==> {}", innerClazz.getName(), msgCode.getNumber());

                    msgCodeAndMsgBodyMap.put(
                            msgCode.getNumber(),
                            (GeneratedMessageV3) resultObj);

                    msgBodyAndMsgCodeMap.put(
                            innerClazz,
                            msgCode.getNumber());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

    }

    public static Message.Builder getBuilderByMsgCode(int msgCode) {

        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 msg = msgCodeAndMsgBodyMap.get(msgCode);

        if (msg == null) {
            return null;
        }

        return msg.newBuilderForType();
    }

    public static int getMsgCodeByMsg(Class<?> msgClazz) {

        if (msgClazz == null) {
            return -1;
        }

        Integer msgCode = msgBodyAndMsgCodeMap.get(msgClazz);

        if (msgCode == null) {
            return -1;
        }

        return msgCode.intValue();


    }
}
