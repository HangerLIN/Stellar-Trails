package IOCContainer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author lth
 * @version 1.0
 * @description 维护一个全局唯一的IOC容器，能够根据beanName获取对应的实例对象
 * @date 2023/10/26 10:06
 */
public class ApplicationContextHolder implements ApplicationContextAware{

    private static ApplicationContext CONTEXT;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.CONTEXT=applicationContext;
    }

    /**
     * 获取上下文
     * @return
     */
    public static ApplicationContext getContext(){
        return CONTEXT;
    }

    /**
     * 根据beanName获取实例对象
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return CONTEXT.getBean(beanName);
    }

    /**
     * 根据类型type获取实例对象
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    /**
     * 根据beanName和类型获取实例对象
     * @param beanName
     * @param clazz
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return CONTEXT.getBean(beanName, clazz);
    }

    /**
     * 获取一个 Map，指定类型的所有 Bean：key 是 Bean 的名字，value 是 Bean 实例
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return CONTEXT.getBeansOfType(clazz);
    }

    /**
     * 查询指定名称的 Bean 是否存在指定的注解，如果存在，返回该注解，否则返回 null
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return CONTEXT.findAnnotationOnBean(beanName, annotationType);
    }

}
