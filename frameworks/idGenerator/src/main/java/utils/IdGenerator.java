package utils;

/**
 * ID 生成器
 * @Author lth
 * @Date 2023/11/1 11:38
 */
public interface IdGenerator {

    /**
     * 下一个 ID
     */
    default long nextId() {
        return 0L;
    }

    /**
     * 下一个 ID 字符串
     */
    default String nextIdStr() {
        return "";
    }
}