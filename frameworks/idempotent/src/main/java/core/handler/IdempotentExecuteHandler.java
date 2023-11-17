package core.handler;

import annotation.Idempotent;
import core.aspect.RepeatConsumptionException;
import core.wrapper.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * The IdempotentExecuteHandler interface provides methods for handling idempotent operations.
 * These operations include handling the idempotent parameter wrapper, executing the idempotent process,
 * handling exceptions during the idempotent process, and post-processing tasks after the idempotent process.
 *
 * @author lth
 * @version 2023.11.16
 */
public interface IdempotentExecuteHandler {

    /**
     * Handles the idempotent parameter wrapper.
     *
     * @param wrapper The IdempotentParamWrapper object to be handled.
     */
    void handler(IdempotentParamWrapper wrapper);

    /**
     * Executes the idempotent process.
     *
     * @param joinPoint The join point at which the idempotent aspect is applied.
     * @param idempotent The idempotent annotation applied at the join point.
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws RepeatConsumptionException;

    /**
     * Handles any exceptions that occur during the idempotent process.
     */
    void exceptionProcessing();

    /**
     * Handles any post-processing tasks after the idempotent process.
     */
    void postProcessing();
}
