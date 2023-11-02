package builder;

import java.io.Serializable;

/**
 * @Classname Builder
 * @Description
 * @Date 2023/10/30 13:19
 * @Created by lth
 */
//沃特玛的是傻逼!接口只能继承别的类,接口!万万不能够将接口继承接口
public interface Builder<T> extends Serializable {
    T build();
}
