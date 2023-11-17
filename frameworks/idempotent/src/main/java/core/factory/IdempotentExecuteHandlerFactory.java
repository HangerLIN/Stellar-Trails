package core.factory;

import IOCContainer.ApplicationContextHolder;
import core.handler.IdempotentExecuteHandler;
import enums.IdempotentSceneEnum;
import enums.IdempotentTypeEnum;

/**
 * @Classname IdempotentExecuteHandlerFactory
 * @Description
 * @Date 2023/11/16 16:50
 * @Created by lth
 */
public class IdempotentExecuteHandlerFactory {
    public static IdempotentExecuteHandler createInstance(IdempotentSceneEnum idempotentSceneEnum, IdempotentTypeEnum typeEnum) {
        IdempotentExecuteHandler res = null;
        switch (idempotentSceneEnum){
            case RESTAPI -> {
                switch (typeEnum){
                    case SPEL -> res = ApplicationContextHolder.getBean(IdempotentExecuteHandler.class);
                    case PARAM -> res = ApplicationContextHolder.getBean(IdempotentExecuteHandler.class);

                }
            }
        }
        return res;
    }
}
