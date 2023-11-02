package config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import handler.MOHandler;
import org.springframework.context.annotation.Bean;

/**
 * @Classname MybatisAutoConfiguration
 * @Description 实现mybatis的自动配置
 * @Date 2023/10/30 11:34
 * @Created by lth
 */
public class MybatisAutoConfiguration {
    /**
     * Mybatis plus分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = mybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 元数据填充
     * @return
     */
    @Bean
    public MOHandler moHandler(){
        return new MOHandler();
    }

    /**
     * 自定义雪花算法 ID 生成器,随机生成ID填充
     * @return
     */
//    @Bean
//    @Primary
//    public MOHandler moHandler(){
//        return new MOHandler();
//    }
}
