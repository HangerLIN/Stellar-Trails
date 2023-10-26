package function;

/**
 * @author lth
 * @version 1.0
 * @description 缓存加载器，如果不存在则加载
 * @date 2023/10/26 16:22
 */
@FunctionalInterface
public interface CacheLoader<T> {
    /**
     * 加载缓存
     * @return <T> 缓存类型
     */
    T load();
}
