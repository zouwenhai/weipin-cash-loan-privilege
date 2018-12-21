package nirvana.cash.loan.privilege.controller.springmvc.base;

import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.exception.LoginSesstionTimeOutException;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.HashMap;
import java.util.Map;


public class BaseController {
    public static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    public RedisService redisService;

    @Autowired
    public RequestCheck requestCheck;

    public Map<String, Object> getDataTable(PageInfo<?> pageInfo) {
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", pageInfo.getList());
        rspData.put("total", pageInfo.getTotal());
        rspData.put("pages", pageInfo.getPages());
        rspData.put("pageSize", pageInfo.getPageSize());
        rspData.put("pageNum", pageInfo.getPageNum());
        return rspData;
    }

    public User getLoginUser(ServerHttpRequest request) {
        User user =  requestCheck.getLoginUser(request);
        if(user != null){
            return user;
        }
        throw LoginSesstionTimeOutException.newInstance(ResResult.LOGIN_SESSION_TIMEOUT,"登录失效");
    }
}
