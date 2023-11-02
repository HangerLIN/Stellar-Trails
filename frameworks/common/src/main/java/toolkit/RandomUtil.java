package toolkit;

import IOCContainer.ApplicationContextHolder;
import java.util.Random;

public class RandomUtil {

    // 内部持有的Random实例
    private static Random RANDOM_INSTANCE;

    static {
        // 从Spring IOC容器中获取Random实例
        RANDOM_INSTANCE = ApplicationContextHolder.getBean(Random.class);
        if (RANDOM_INSTANCE == null) {
            // 如果容器中没有Random实例，则使用默认构造器创建一个
            RANDOM_INSTANCE = new Random();
        }
    }

    /**
     * 生成一个指定范围内的随机整数
     *
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 在[min, max]范围内的随机整数
     */
    public static long getRandomInt(int min, int max) {
        return RANDOM_INSTANCE.nextLong(max - min + 1) + min;
    }

}
