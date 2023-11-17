package core.wrapper;

import annotation.Idempotent;
import enums.IdempotentTypeEnum;
import lombok.*;
import lombok.experimental.Accessors;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @Classname IdempotentParamWrapper
 * @Description
 * @Date 2023/11/16 16:44
 * @Created by lth
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class IdempotentParamWrapper {
    private Idempotent idempotent;

    private ProceedingJoinPoint joinPoint;

    /**
     * 锁标识，{@link IdempotentTypeEnum#PARAM}
     */
    private String lockKey;
}
