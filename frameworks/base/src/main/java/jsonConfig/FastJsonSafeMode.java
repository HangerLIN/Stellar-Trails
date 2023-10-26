package jsonConfig;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2023/10/26 14:36
 */
public class FastJsonSafeMode implements InitializingBean {
    /**
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("FastJsonSafeMode.afterPropertiesSet");
        System.setProperty("fastjson.parser.safeMode", "true");
    }
}
