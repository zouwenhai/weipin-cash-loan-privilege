package nirvana.cash.loan.privilege.system.service.impl;

import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.service.RedisService;
import nirvana.cash.loan.privilege.system.service.LogoutUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2018/8/29.
 */
@Service
public class LogoutUserServiceImpl implements LogoutUserService {
    public static final Logger logger = LoggerFactory.getLogger(LogoutUserServiceImpl.class);
    @Autowired
    public RedisService redisService;

    //查询所有登录用户jsessionid
    @Override
    public Set<String> findAllLoginJsessionid() {
        Set<String> res = new HashSet<>();
        try {
            String prefix = RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX;
            Set<String> keys = redisService.getKeysWithPattern(prefix + "*");
            if (keys != null && keys.size() > 0) {
                Iterator<String> it = keys.iterator();
                while (it.hasNext()) {
                    String key = it.next().replace(prefix, "");
                    res.add(key);
                }
            }
        } catch (Exception ex) {
            logger.error("查询所有登录用户jsessionid失败:{}", ex);
        }
        return res;
    }

    //查询指定用户jsessionid
    @Override
    public String findJsessionidByUserId(Long userId, Set<String> jsessionids) {
        String res = null;
        try {
            if (jsessionids == null || jsessionids.size() == 0) {
                jsessionids = findAllLoginJsessionid();
            }
            Iterator<String> it = jsessionids.iterator();
            while (it.hasNext()) {
                String jessionId = it.next();
                if (jessionId.split("-")[0].equals(userId.toString())) {
                    res = jessionId;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("查询指定用户jsessionid失败:{}", ex);
        }
        return res;
    }

    //退出指定登录用户
    @Override
    public void logoutUser(Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        this.batchLogoutUser(userIds);
    }

    //批量退出登录用户
    @Override
    public void batchLogoutUser(List<Long> userIdList) {
        Set<String> keys = this.findAllLoginJsessionid();
        if (userIdList != null && userIdList.size() > 0) {
            for (long userId : userIdList) {
                String jsessionid = findJsessionidByUserId(userId, keys);
                if (StringUtils.isNotBlank(jsessionid)) {
                    redisService.delete(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX + jsessionid);
                }
            }
        }
    }


}
