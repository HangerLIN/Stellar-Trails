package singleton;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2023/10/26 12:41
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Singleton {

    /**
     * 维护一个单例池，Key为类名（所以不需要自己去单独声明），Value为实例对象
     */
    private static final ConcurrentHashMap<String, Object> SINGLETON_POOL = new ConcurrentHashMap<>();

    /**
     * 通过type获取单例对象
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(Class<T> clazz){
        String key = clazz.getName();
        if(SINGLETON_POOL.containsKey(key)){
            return (T) SINGLETON_POOL.get(key);
        }
        synchronized (SINGLETON_POOL){
            // 双重检查
            if(SINGLETON_POOL.containsKey(key)){
                return (T) SINGLETON_POOL.get(key);
            }
            T instance = null;
            try {
                instance = clazz.newInstance();
                SINGLETON_POOL.put(key, instance);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return instance;
        }
    }

    /**
     * 通过key: beanName获取单例对象，如果不存在则通过supplier创建一个
     * @param key
     * @return
     */
    public static <T> T get(String key, Supplier<T> supplier) {
        Object res = SINGLETON_POOL.get(key);
        if(res != null) {
            return (T) res;
        }
        res = supplier.get();
//      REVISE:使用自定义的 Put 方法，自动获取类名（但是这个效果需要进行多一次反射，造成性能损耗）
//        put(res);
        SINGLETON_POOL.put(key, res);
        return res != null ? (T) res : null;
    }

    /**
     * 放入单例对象到单例池
     * @param Value
     */
    public static void put(Object Value){
        SINGLETON_POOL.put(Value.getClass().getName(), Value);
    }

    public static void put(String key, Object Value){
        SINGLETON_POOL.put(key, Value);
    }
}
