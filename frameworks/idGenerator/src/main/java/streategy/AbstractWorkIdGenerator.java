package streategy;

import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import snowflake.Snowflake;
import toolkits.SnowflakeIdUtil;
import wrapper.WorkIdWrapper;

/**
 * @Classname AbstractWorkIdGenerator
 * @Description
 * @Date 2023/11/1 11:23
 * @Created by lth
 */
@Slf4j
public abstract class AbstractWorkIdGenerator {
    /**
     * 是否使用 {@link SystemClock} 获取当前时间戳
     */
    @Value("${framework.distributed.id.snowflake.is-use-system-clock:false}")
    private boolean isUseSystemClock;

    /**
     * 根据自定义策略获取 WorkId 生成器
     *
     * @return
     */
    protected static WorkIdWrapper chooseWorkId() {
        return null;
    }

    public void init(){
        WorkIdWrapper workIdWrapper = chooseWorkId();
        long workId = workIdWrapper.getWorkId();
        long dataCenterId = workIdWrapper.getDataCenterId();
        Snowflake snowflake = new Snowflake(workId, dataCenterId, isUseSystemClock);
        log.info("Snowflake type: {}, workId: {}, dataCenterId: {}", this.getClass().getSimpleName(), workId, dataCenterId);
        SnowflakeIdUtil.initSnowflake(snowflake);
    }
}
