package algorithm;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Getter;
import org.apache.shardingsphere.infra.util.exception.ShardingSpherePreconditions;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmInitializationException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @Description: 訂單表的複合分片算法配置，结合订单号和用户id进行分片，订单号后6位进行分片，用户id后6位进行分片，分片数量为8，表分片数量为4
 * @Author: lth
 * @Date: 2021/10/27 20:32
 * @Version: V1.0
 */
public class OrderCommonDataBaseComplexAlgorithm implements ComplexKeysShardingAlgorithm {

    @Getter
    private Properties props;

    private int shardingCount;
    private int tableShardingCount;

    private static final String SHARDING_COUNT_KEY = "sharding-count";
    private static final String TABLE_SHARDING_COUNT_KEY = "table-sharding-count";

    /**
     * @param collection
     * @param complexKeysShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        Map<String,Collection<Comparable<Long>>> columnNameAndShardingValuesMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
        Collection<String> result = new java.util.LinkedHashSet<>(collection.size());
        if (CollUtil.isNotEmpty(columnNameAndShardingValuesMap)) {
            String userId = "user_id";
            // 获取用户id集合,发现只要在上面的Map的位置已经事先定义了，那么这里就不会报错编译错误
            Collection<Comparable<Long>> customerUserIdCollection = columnNameAndShardingValuesMap.get(userId);
            if (CollectionUtils.isNotEmpty(customerUserIdCollection)) {
                String dbSuffix;
                Comparable<?> comparable = customerUserIdCollection.stream().findFirst().get();
                if (comparable instanceof String) {
                    String actualUserId = comparable.toString();
                    dbSuffix = String.valueOf(hashShardingValue(actualUserId.substring(Math.max(actualUserId.length() - 6, 0))) % shardingCount / tableShardingCount);
                } else {
                    dbSuffix = String.valueOf(hashShardingValue((Long) comparable % 1000000) % shardingCount / tableShardingCount);
                }
                result.add("ds_" + dbSuffix);
            } else {
                String orderSn = "order_sn";
                String dbSuffix;
                Collection<Comparable> orderSnCollection = (Collection) columnNameAndShardingValuesMap.get(orderSn);
                Comparable comparable = (Comparable) orderSnCollection.stream().findFirst().get();
                if (comparable instanceof String) {
                    String actualOrderSn = comparable.toString();
                    dbSuffix = String.valueOf(hashShardingValue(actualOrderSn.substring(Math.max(actualOrderSn.length() - 6, 0))) % shardingCount / tableShardingCount);
                } else {
                    dbSuffix = String.valueOf(hashShardingValue((Long) comparable % 1000000) % shardingCount / tableShardingCount);
                }
                result.add("ds_" + dbSuffix);
            }
        }
    }

    /**
     * @param dataNodePrefix
     * @param shardingColumn
     * @return
     */
    @Override
    public Optional<String> getAlgorithmStructure(String dataNodePrefix, String shardingColumn) {
        return ComplexKeysShardingAlgorithm.super.getAlgorithmStructure(dataNodePrefix, shardingColumn);
    }

    /**
     * @param props
     */
    @Override
    public void init(Properties props) {
        this.props = props;
        shardingCount = getShardingCount(props);
        tableShardingCount = getTableShardingCount(props);
    }

    private int getShardingCount(Properties props) {
        ShardingSpherePreconditions.checkState(props.containsKey(SHARDING_COUNT_KEY),  () -> new ShardingAlgorithmInitializationException(getType(), "Sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    private int getTableShardingCount(Properties props) {
        ShardingSpherePreconditions.checkState(props.containsKey(TABLE_SHARDING_COUNT_KEY), () -> new ShardingAlgorithmInitializationException(getType(), "Table sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(TABLE_SHARDING_COUNT_KEY));
    }

    private long hashShardingValue(final Comparable<?> shardingValue) {
        return Math.abs((long)shardingValue.hashCode());
    }

    /**
     * @return
     */
    @Override
    public String getType() {
        return "CLASS_BASED";
    }

    /**
     * @return
     */
    @Override
    public Collection<String> getTypeAliases() {
        return ComplexKeysShardingAlgorithm.super.getTypeAliases();
    }

    /**
     * @return
     */
    @Override
    public boolean isDefault() {
        return ComplexKeysShardingAlgorithm.super.isDefault();
    }
}
