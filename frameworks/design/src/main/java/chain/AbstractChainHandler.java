package chain;

import org.springframework.core.Ordered;

/**
 * @Classname AbstractChainHandler
 * @Description 抽象业务责任链组件
 * @Date 2023/10/30 13:29
 * @Created by lth
 */
public interface AbstractChainHandler<T> extends Ordered {

    void handler(T requestParam);

    String mark();
}
