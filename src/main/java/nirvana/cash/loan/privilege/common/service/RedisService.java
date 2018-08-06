package nirvana.cash.loan.privilege.common.service;

import java.util.List;

/**
 * Created by Administrator on 2018/7/27.
 */
public interface RedisService {

    <T> boolean put(String key, T obj);

    //缓存时间单位：秒
    <T> void putWithExpireTime(String key, T obj, final long expireTime);

    <T> boolean putList(String key, List<T> objList);

    //缓存时间单位：秒
    <T> boolean putListWithExpireTime(String key, List<T> objList, final long expireTime);

    <T> T get(final String key, Class<T> targetClass);

    <T> List<T> getList(final String key, Class<T> targetClass);

    void delete(String key);

    void deleteWithPattern(String pattern);

    void clear();

    Long getOrderId(String key);
}
