package fhq.demo.redis;

import fhq.demo.util.StrObjTransformerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author fhq
 * @date 2019/12/20 14:47
 */
@Service
public class RedisService {
    private JedisPool jedisPool;
    private RedisConfig redisConfig;

    @Autowired
    public RedisService(JedisPool jedisPool, RedisConfig redisConfig) {
        this.jedisPool = jedisPool;
        this.redisConfig = redisConfig;
    }

    /**
     * 将key对应的值从缓存中取出
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return 缓存中key对应的对象
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            String s = jedis.get(key);

            return StrObjTransformerUtil.stringToBean(s, clazz);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将指定对象以{key: value}的形式放入缓存中
     *
     * @param key   缓存对象对应的键
     * @param value 待放入缓存中的对象
     * @param <T>
     * @return 是否将对象放入到缓存中
     */
    public <T> boolean set(String key, T value) {
        Jedis jedis = null;
        try {
            String s = StrObjTransformerUtil.beanToString(value);
            if (s == null || s.length() <= 0) {
                return false;
            }

            jedis = jedisPool.getResource();

            jedis.setex(key, redisConfig.getTimeout(), s);


            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 缓存中是否存在该key
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            return jedis.exists(key);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将key对应的缓存值加一
     *
     * @param key
     * @return 加一操作后，缓存中key对应的值
     */
    public long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            // 原子操作
            return jedis.incr(key);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将key对应缓存值减一
     *
     * @param key
     * @return 减一操作后，缓存中key对应的值
     */
    public long decr(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            // 原子操作
            return jedis.decr(key);
        } finally {
            returnToPool(jedis);
        }
    }

    public long delete(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            return jedis.del(key);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将Jedis连接对象返回到Jedis连接池
     *
     * @param jedis 待返回 Jedis连接池 的Jeids连接对象
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
