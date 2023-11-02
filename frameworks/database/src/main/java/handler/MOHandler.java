package handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import enums.DelEnum;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @Classname MetaObjectHandler
 * @Description 实现元数据的自动填充
 * @Date 2023/10/30 11:24
 * @Created by lth
 */
public class MOHandler implements MetaObjectHandler {
    /**
     * 插入时的填充策略
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime", Date.class,new Date());
        this.strictInsertFill(metaObject,"updateTime",Date.class,new Date());
        this.strictInsertFill(metaObject,"delFlag",Integer.class, DelEnum.NORMAL.getCode());
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"updateTime",Date.class,new Date());
    }

}
