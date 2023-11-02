package core;

import lombok.Data;

/**
 * @Classname HLogPrintDto
 * @Description print的打印的参数
 * @Date 2023/11/1 22:35
 * @Created by lth
 */
@Data
public class HLogPrintDto {
    private String beginTime;
    private Object[] inputParams;
    private Object outputParams;
}
