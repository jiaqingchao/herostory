package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令处理器工厂
 */
public final class CmdHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private CmdHandlerFactory() {
    }

    public static void init() {
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                CmdHandlerFactory.class.getPackage().getName(),
                false,
                ICmdHandler.class);

        for (Class clazz : clazzSet) {
            if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }
            // 获取方法数组
            Method[] methodArray = clazz.getDeclaredMethods();
            Class<?> msgType = null;

            for (Method currMethod : methodArray) {
                if (!currMethod.getName().equals("handle")) {
                    continue;
                }
                //获取函数参数类型
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();

                if (paramTypeArray.length < 2 ||
                        !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }

                msgType = paramTypeArray[1];
                break;
            }
            if (msgType == null) {
                continue;
            }

            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.getDeclaredConstructor().newInstance();

                LOGGER.info("{} <==> {}", msgType.getName(), clazz.getName());

                handlerMap.put(msgType, newHandler);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (msgClazz == null) {
            return null;
        }

        return handlerMap.get(msgClazz);
    }
}
