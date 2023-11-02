package toolkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class RandomUtil_0 {

    private final Random RANDOM;

    private static final RandomUtil_0 INSTANCE = new RandomUtil_0(new Random());

    // 私有构造方法，用于依赖注入
    private RandomUtil_0(Random random) {
        this.RANDOM = random;
    }

    // 静态方法返回唯一实例
    public static RandomUtil_0 getInstance() {
        return INSTANCE;
    }

    public int getRandomInt(int min, int max) {
        return RANDOM.nextInt(max - min + 1) + min;
    }
}
