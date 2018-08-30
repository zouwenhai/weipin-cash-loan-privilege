package nirvana.cash.loan.privilege.web.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.LogService;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 用zuulFilter打印请求日志
 * 实现参考:
 * (1)https://blog.csdn.net/kysmkj/article/details/79159421
 * (2)https://www.hhfate.cn/t/598
 */
public class AccessZullFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AccessZullFilter.class);

    @Autowired
    private RequestCheck requestCheck;
    @Autowired
    private LogService logService;


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        try {
            long startTime = System.currentTimeMillis();
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            //请求方法
            String method = request.getMethod();
            //请求地址
            String url = request.getRequestURL().toString();
            logger.info("PreRequestLogFilter|run|请求方法和地址:method={},url={}", method, url);
            //请求url参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuilder sb = new StringBuilder();
            sb.append("urlParam=\t");
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                //密码不能输出到日志
                if(!entry.getKey().contains("password")){
                    sb.append("[" + entry.getKey() + "=" + requestCheck.printArray(entry.getValue()) + "]");
                }
            }
            logger.info("PreRequestLogFilter|run|请求url参数:{}", sb.toString());
            //请求json参数
            String jsonParam=null;
            if(request.getContentType()!=null && !request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                InputStream in = request.getInputStream();
                jsonParam = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
                logger.info("PreRequestLogFilter|run|请求json参数:{}", jsonParam);
            }
            //check登录和权限
            ResResult res = requestCheck.check(request);
            if (!res.getCode().equals(ResResult.SUCCESS)) {
                //过滤该请求，不往下级服务去转发请求，到此结束
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(Integer.valueOf(res.getCode()));
                ctx.setResponseBody(JSON.toJSONString(res));
                ctx.getResponse().setContentType("application/json;charset=UTF-8");
                return null;
            }
            User user = (User) res.getData();
            //添加请求头参数
            ctx.addZuulRequestHeader("loginName",user.getUsername());
            ctx.addZuulRequestHeader("userName",user.getName());
            long endTime = System.currentTimeMillis();

            //记录访问日志
           logService.addLog(user.getUsername(),url,endTime-startTime,sb.toString()+"|jsonParam="+jsonParam);

        } catch (Exception ex) {
            logger.error("PreRequestLogFilter|run|执行异常:{}", ex);
        }
        return null;
    }

}