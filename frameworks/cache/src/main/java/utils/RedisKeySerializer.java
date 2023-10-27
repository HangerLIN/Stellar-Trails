package utils;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

@RequiredArgsConstructor
public class RedisKeySerializer implements InitializingBean, RedisSerializer<String> {

    private final String keyPrefix;

    private final String charsetName;

    private Charset charset;

    /**
     * Serialize the given object to binary data.
     * @param key object to serialize. Can be {@literal null}.
     * @return
     * @throws SerializationException
     */
    @Override
    public byte[] serialize(String key) throws SerializationException {
        String builderKey = keyPrefix + key;
        return builderKey.getBytes();
    }

    /**
     * Deserialize an object from the given binary data.
     * @param bytes object binary representation. Can be {@literal null}.
     * @return
     * @throws SerializationException
     */
    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        return new String(bytes, charset);
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied (and satisfied) by BeanFactory itself and
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        charset = Charset.forName(charsetName);
    }
}
