package wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Classname WorkIdWrapper
 * @Description
 * @Date 2023/11/1 11:14
 * @Created by lth
 */
@Data
@AllArgsConstructor
public class WorkIdWrapper {
    private Long workId;
    private Long dataCenterId;
}
