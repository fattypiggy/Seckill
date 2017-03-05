package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by williamjing on 2017/3/5.
 */
public class RedisDao {

    private final JedisPool jedisPool;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(long seckillId) {
        //get byte[]->Object(Seckill)
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillId:" + seckillId;
                //Redis 并没有实现序列化
                //所以采用自定义序列化 Protostuff
                byte[] bytes = jedis.get(key.getBytes());
                if (bytes != null) {
                    //缓存中找到
                    Seckill seckill = schema.newMessage();
                    //seckill被反序列化
                    ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String setSeckill(Seckill seckill) {
        //set Object(Seckill) ->byte[] 序列化
        try {
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "seckillId:" + seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60 * 60;//An hour
                //超时缓存
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
