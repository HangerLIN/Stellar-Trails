import chain.AbstractChainContext;
import config.ApplicationBaseAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import strategy.AbstractStrategyChoose;

/**
 * 自动装配实现
 */
@ImportAutoConfiguration(ApplicationBaseAutoConfiguration.class)
public class DesignPatternAutoConfiguration {

    /**
     * 策略模式选择器
     */
    @ConditionalOnMissingBean
    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

    /**
     * 责任链上下文
     */
    @ConditionalOnMissingBean
    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }
}