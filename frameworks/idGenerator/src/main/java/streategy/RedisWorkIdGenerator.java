package streategy;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import wrapper.WorkIdWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname RedisWorkIdGenerator
 * @Description
 * @Date 2023/11/1 11:25
 * @Created by lth
 */
@Slf4j
public class RedisWorkIdGenerator extends AbstractWorkIdGenerator implements InitializingBean {
    private static StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

    public RedisWorkIdGenerator(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * @throws Exception
     */
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
        if (stringRedisTemplate == null) {
            log.warn("StringRedisTemplate is null, falling back to RandomWorkIdChoose");
            return RandomWorkIdGenerator.chooseWorkId();
        }
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/chooseWorkIdLua.lua")));
        ArrayList<Long> res = null;
        try {
            redisScript.setResultType(List.class);
            res = (ArrayList) stringRedisTemplate.execute(redisScript, null);
        }catch (Exception e){
            log.error("Redis Lua 脚本获取ID失败",e);
        }
        //说明上面还是失败, 继续使用上面的随机id生成器进行ID的生成
        return CollUtil.isNotEmpty(res) ? new WorkIdWrapper(res.get(0), res.get(1)): RandomWorkIdGenerator.chooseWorkId();
    }



}

