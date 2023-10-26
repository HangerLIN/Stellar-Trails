package config;

import IOCContainer.ApplicationContextHolder;
import IOCContainer.ApplicationContextPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author lth
 * @version 1.0
 * @description 自动装配-基础组件
 * @date 2023/10/26 10:43
 */
public class ApplicationBaseAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder(){
        return new ApplicationContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextPostProcessor applicationContextPostProcessor(ApplicationContext applicationContext){
        return new ApplicationContextPostProcessor(applicationContext);
    }
}
