package core.handler.tokenHandler;

import core.service.IdempotentTokenService;
import exception.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import result.Result;

/**
 * @Classname IdempotentTokenController
 * @Description
 * @Date 2023/11/17 17:24
 * @Created by lth
 */
@RestController
@RequiredArgsConstructor
public class IdempotentTokenController {

    private final IdempotentTokenService idempotentTokenService;

    /**
     * 请求申请Token
     */
    @GetMapping("/token")
    public Result<String> createToken() {
        return Results.success(idempotentTokenService.createToken());
    }
}
