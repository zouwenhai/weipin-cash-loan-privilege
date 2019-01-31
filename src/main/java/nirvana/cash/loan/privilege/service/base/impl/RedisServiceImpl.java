package nirvana.cash.loan.privilege.service.base.impl;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ProtoStuffSerializerUtil;
import nirvana.cash.loan.privilege.service.base.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    //缓存名
    public final static String CAHCENAME = "privilege";
    //默认缓存时间
    public final static int CAHCETIME = 60;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public <T> boolean put(String key, T obj) {
        this.delete(key);
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serialize(obj);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(bkey, bvalue);
            }
        });
        return result;
    }

    @Override
    public <T> void putWithExpireTime(String key, T obj, final long expireTime) {
        this.delete(key);
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serialize(obj);
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
    }

    @Override
    public <T> boolean putList(String key, List<T> objList) {
        this.delete(key);
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(bkey, bvalue);
            }
        });
        return result;
    }

    @Override
    public <T> boolean putListWithExpireTime(String key, List<T> objList,
                                             final long expireTime) {
        final byte[] bkey = key.getBytes();
        final byte[] bvalue = ProtoStuffSerializerUtil.serializeList(objList);
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(bkey, expireTime, bvalue);
                return true;
            }
        });
        return result;
    }

    @Override
    public <T> T get(final String key, Class<T> targetClass) {
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes());
            }
        });
        if (result == null) {
            return null;
        }
        return ProtoStuffSerializerUtil.deserialize(result, targetClass);
    }

    @Override
    public <T> List<T> getList(final String key, Class<T> targetClass) {
        byte[] result = redisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.get(key.getBytes());
            }
        });
        if (result == null) {
            return null;
        }
        return ProtoStuffSerializerUtil.deserializeList(result, targetClass);
    }

    /**
     * 精确删除key
     */
    @Override
    public void delete(String key) {
        try{
            redisTemplate.delete(key);
        }catch (Exception ex){
            log.error("redis删除发生异常:{}",ex);
        }
    }

    /**
     * 模糊删除key
     */
    @Override
    public void deleteWithPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() {
        deleteWithPattern(CAHCENAME + "|*");
    }

    /**
     * 订单获取自增主键
     */
    @Override
    public Long getOrderId(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(CAHCENAME + "|" + key,
                redisTemplate.getConnectionFactory());
        entityIdCounter.expire(1, TimeUnit.DAYS);
        Long increment = entityIdCounter.incrementAndGet();
        return increment;
    }

    @Override
    public Set<String> getKeysWithPattern(String pattern) {
         return redisTemplate.keys(pattern);
    }

    @Override
    public void deleteWithKeys(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 添加Set
     */
    @Override
    public long putSet(String key, String[] data) {
        return redisTemplate.opsForSet().add(key, data);
    }

    /**
     * 返回集中所有元素
     */
    @Override
    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 移除Set指定元素
     */
    @Override
    public long remove(String key,String[] data) {
        return redisTemplate.opsForSet().remove(key,data);
    }

    /**
     * 从Set中弹出一个元素
     * @param key
     * @return
     */
    @Override
    public String pop(String key){
        return redisTemplate.opsForSet().pop(key);
    }

}