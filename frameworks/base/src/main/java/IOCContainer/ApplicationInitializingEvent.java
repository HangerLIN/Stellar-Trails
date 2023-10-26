package IOCContainer;

import org.springframework.context.ApplicationEvent;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2023/10/26 10:45
 */
public class ApplicationInitializingEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ApplicationInitializingEvent(Object source) {
        super(source);
    }
}

