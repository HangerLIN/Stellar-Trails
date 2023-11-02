package streategy;

//import cn.hutool.core.util.RandomUtil;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import toolkit.RandomUtil;
import wrapper.WorkIdWrapper;

/**
 * @Classname RandomWorkerIdGenerator
 * @Description
 * @Date 2023/11/1 11:25
 * @Created by lth
 */
@AllArgsConstructor
public class RandomWorkIdGenerator extends AbstractWorkIdGenerator implements InitializingBean{
    /**
     * @throws Exception
     */
    private static final int START = 0;
    private static final int END = 31;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * 根据自定义策略获取 WorkId 生成器
     *
     * @return
     */
    protected static WorkIdWrapper chooseWorkId() {
        // return new WorkIdWrapper(RandomUtil.randomInt()) 这里才发现可以使用的hutool的工具类...以后还是看看,不要重复去制造轮子
        return new WorkIdWrapper(RandomUtil.getRandomInt(START,END), RandomUtil.getRandomInt(START, END));
    }
}
