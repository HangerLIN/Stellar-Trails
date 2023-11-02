package chain;

import IOCContainer.ApplicationContextHolder;
import com.google.common.collect.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;

import java.util.*;

/**
 * @author lth
 * @Classname AbstractChainContext
 * @Description 抽象责任链上下文
 * @Date 2023/10/30 13:29
 */
public class AbstractChainContext<T> implements CommandLineRunner {

    /**
     * key: 责任链组件标识
     * value: 责任链组件
     */
    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerMap = Maps.newHashMap();

    public void handler(String mark, T requestParams) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerMap.get(mark);
        if (abstractChainHandlers == null) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        abstractChainHandlers.forEach(each -> each.handler(requestParams));
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder
                .getBeansOfType(AbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerMap.computeIfAbsent(bean.mark(), k -> new ArrayList<>());
            // 将新的bean插入到适当的位置以保持列表的有序性
            int index = Collections.binarySearch(abstractChainHandlers, bean, Comparator.comparing(Ordered::getOrder));
            if (index < 0) {
                //如果不存在,那么获得是负数;取反码得到正确的插入位置
                index = ~index;
            }
            abstractChainHandlers.add(index, bean);
        });
    }

}
