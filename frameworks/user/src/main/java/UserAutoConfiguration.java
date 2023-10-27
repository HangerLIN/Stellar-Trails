import core.UserTransmitFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2023/10/26 14:50
 */
@ConditionalOnWebApplication
public class UserAutoConfiguration {
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> loginFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        registration.addUrlPatterns("/*");
        registration.setName("loginFilter");
        registration.setOrder(1);
        return registration;
    }
}
