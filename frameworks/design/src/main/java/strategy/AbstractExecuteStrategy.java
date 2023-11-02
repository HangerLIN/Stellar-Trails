package strategy;

/**
 * @Classname AbstractExecuteStrategy
 * @Description
 * @Date 2023/10/31 17:34
 * @Created by lth
 */
public interface AbstractExecuteStrategy<REQUEST, RESPONSE> {
    /**
     * 返回执行的策略标识,在本项目中,一般是匹配和对应的table对应的数据库处理策略
     * @return
     */
    default String mark() {
        return this.getClass().getSimpleName();
    }

    /**
     * 模式匹配方法;在本项目中,一般是进行匹配的正则表达式
     * @return
     */
    default String patternMatchMark() {
        return this.getClass().getSimpleName();
    }

    void execute(REQUEST requestParam);

    RESPONSE executeResp(REQUEST requestParam);
}
