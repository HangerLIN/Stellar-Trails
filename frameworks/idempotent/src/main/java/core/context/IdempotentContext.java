package core.context;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Classname IdempotentContext
 * @Description
 * @Date 2023/11/16 17:36
 * @Created by lth
 */
public class IdempotentContext {
    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    /**
     * This method is used to get the context map.
     * @return The context map.
     */
    public static Map<String,Object> get(){
        return CONTEXT.get();
    }

    /**
     * This method is used to get the value of a specific key from the context map.
     * @param key The key to get the value for.
     * @return The value of the specified key.
     */
    public static Object getKey(String key) {
        Map<String, Object> context = get();
        if (CollUtil.isNotEmpty(context)) {
            return context.get(key);
        }
        return null;
    }

    /**
     * This method is used to get the string representation of the value of a specific key from the context map.
     * @param key The key to get the value for.
     * @return The string representation of the value of the specified key.
     */
    public static String getString(String key) {
        Object actual = getKey(key);
        if (actual != null) {
            return actual.toString();
        }
        return null;
    }

    /**
     * This method is used to put a key-value pair into the context map.
     * @param key The key to put.
     * @param val The value to put.
     */
    public static void put(String key, Object val) {
        Map<String, Object> context = get();
        if (CollUtil.isEmpty(context)) {
            context = Maps.newHashMap();
        }
        context.put(key, val);
        putContext(context);
    }

    /**
     * This method is used to put a context map into the thread local.
     * @param context The context map to put.
     */
    public static void putContext(Map<String, Object> context) {
        Map<String, Object> threadContext = CONTEXT.get();
        if (CollUtil.isNotEmpty(threadContext)) {
            threadContext.putAll(context);
            return;
        }
        CONTEXT.set(context);
    }

    /**
     * This method is used to clean the context map from the thread local.
     */
    public static void clean() {
        CONTEXT.remove();
    }
}