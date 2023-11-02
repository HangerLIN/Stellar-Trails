package strategy;

import IOCContainer.ApplicationContextHolder;
import IOCContainer.ApplicationInitializingEvent;
import com.google.common.collect.Maps;
import exception.ServiceException;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author lth
 * @Classname AbstractStrategyChoose
 * @Description
 * @Date 2023/10/31 17:34
 */
public class AbstractStrategyChoose implements ApplicationListener<ApplicationInitializingEvent> {

    private final Map<String, AbstractExecuteStrategy> abstractExecuteStrategyMap = Maps.newHashMap();

    /**
     * 根据 mark 查询具体策略,从Map里面获取
     * @param mark
     * @param predicateFlag
     * @return
     */
    public AbstractExecuteStrategy choose(String mark, Boolean predicateFlag) {
        if (predicateFlag != null && predicateFlag) {
            return abstractExecuteStrategyMap.values().stream()
                    .filter(each -> each.patternMatchMark().equals(mark))
                    .filter(each -> Pattern.compile(each.patternMatchMark()).matcher(mark).matches())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("策略未定义"));
        } else {
            return Optional.ofNullable(abstractExecuteStrategyMap.get(mark))
                    .orElseThrow(() -> new RuntimeException(String.format("[%s] 策略未定义", mark)));
        }
    }

    /**
     * 根据 mark 查询具体策略并执行
     * @param mark
     * @param requestParam
     * @param <REQUEST>
     */
    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark, null);
        executeStrategy.execute(requestParam);
    }

    /**
     * 带返回值的策略执行
     */
    public <REQUEST, RESPONSE> RESPONSE chooseAndExecuteResp(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy<REQUEST, RESPONSE> executeStrategy = choose(mark, null);
        return executeStrategy.executeResp(requestParam);
    }

    /**
     * 加载内容
     * 监听器模式,在项目启动的时候,将所有的策略类加载到Map里面
     * 注释:这里的业务逻辑类似前面的责任链模式,都是在项目启动的时候,将所有的策略类加载到Map里面,然后根据mark进行匹配
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        Map<String, AbstractExecuteStrategy> executeStrategyMap = ApplicationContextHolder
                .getBeansOfType(AbstractExecuteStrategy.class);
        executeStrategyMap.forEach((beanName, bean) -> {
            AbstractExecuteStrategy beanExist = abstractExecuteStrategyMap.get(bean.mark());
            if (executeStrategyMap != null) {
                throw new ServiceException("策略被重复定义");
            }
            abstractExecuteStrategyMap.put(beanExist.mark(), bean);
        } );
    }
}
