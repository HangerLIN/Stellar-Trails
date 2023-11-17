package core.handler;

import annotation.Idempotent;
import core.wrapper.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Classname AbstractIdempotentExecuteHandler
 * @Description
 * @Date 2023/11/16 14:01
 * @Created by lth
 */
public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler{

    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent)  {
        //模板方法
        IdempotentParamWrapper idempotentParamWrapper = buildWrapper(joinPoint)
                .setIdempotent(idempotent);

        handler(idempotentParamWrapper);
    }

    protected abstract IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint);

    public void exceptionProcessing() {

    }

    public void postProcessing() {

    }
}
