package nirvana.cash.loan.privilege.common.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.service.RedisService;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.system.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class BaseController {
    public static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public final static String JSESSIONID = "JSESSIONID";

    @Autowired
    public RedisService redisService;

    protected Map<String, Object> getDataTable(PageInfo<?> pageInfo) {
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", pageInfo.getList());
        rspData.put("total", pageInfo.getTotal());
        rspData.put("pages", pageInfo.getPages());
        rspData.put("pageSize", pageInfo.getPageSize());
        rspData.put("pageNum", pageInfo.getPageNum());
        return rspData;
    }

    public User getLoginUser(HttpServletRequest request) {
        User user = null;
        try {
            String jsessionid = CookieUtil.getCookieValue(request, JSESSIONID);
            String data = redisService.get(jsessionid,String.class);
            user = JSON.parseObject(data, User.class);
        } catch (Exception ex) {
        }
        return user;
    }
}
