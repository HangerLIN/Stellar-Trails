package IOCContainer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lth
 * @version 1.0
 * @description 应用初始化后置处理器，防止 Spring 事件被多次
 * @date 2023/10/26 10:39
 */
@RequiredArgsConstructor
public class ApplicationContextPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext applicationContext;

    /**
     * 执行标识，确保 Spring {@link ApplicationReadyEvent} 事件只被执行一次
     */
    private final AtomicBoolean executeOnlyOnce = new AtomicBoolean(false);

    /**
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 防止 Spring 事件被多次执行, 通过 CAS 操作确保只被执行一次
        if (!executeOnlyOnce.compareAndSet(false, true)) {
            return;
        } applicationContext.publishEvent(new ApplicationInitializingEvent(this));
    }
}
