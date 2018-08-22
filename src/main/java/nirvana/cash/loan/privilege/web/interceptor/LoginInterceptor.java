package nirvana.cash.loan.privilege.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.LogService;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RequestCheck requestCheck;
    @Autowired
    private LogService logService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            //请求方法
            String method = request.getMethod();
            //请求地址
            String url = request.getRequestURL().toString();
            logger.info("LoginInterceptor|preHandle|请求方法和地址:method={},url={}", method, url);
            //请求url参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuilder sb = new StringBuilder();
            sb.append("urlParam=\t");
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                //密码不能输出到日志
                if(!entry.getKey().contains("password")){
                    sb.append("[" + entry.getKey() + "=" + printArray(entry.getValue()) + "]");
                }
            }
            logger.info("LoginInterceptor|preHandle|请求url参数:{}", sb.toString());
            //请求json参数
            String jsonParam=null;
            if(request.getContentType()!=null && !request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                InputStream in = request.getInputStream();
                jsonParam = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
                if(StringUtils.isNotBlank(jsonParam) && jsonParam.contains("password")){
                     JSONObject json = JSON.parseObject(jsonParam);
                     json.put("password",null);
                     json.put("newpassword",null);
                     jsonParam=json.toJSONString();
                    logger.info("LoginInterceptor|preHandle|请求json参数:{}", jsonParam);
                }
            }

            ResResult res = requestCheck.check(request);
            if (!res.getCode().equals(ResResult.SUCCESS)) {
                PrintWriter writer = response.getWriter();
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                writer.append(JSONObject.toJSONString(res));
                writer.flush();
                writer.close();
                return false;
            }
            User user = (User) res.getData();
            request.setAttribute("username", user.getUsername());

            long endTime = System.currentTimeMillis();

            //记录访问日志
            logService.addLog(user.getUsername(),url,endTime-startTime,sb.toString()+"|jsonParam="+jsonParam);

        } catch (Exception ex) {
            logger.error("LoginInterceptor|preHandle|执行异常:{}", ex);
            PrintWriter writer = response.getWriter();
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            writer.append(JSONObject.toJSONString(ResResult.error("系统异常")));
            writer.flush();
            writer.close();
            return false;
        }
        return true;
    }

    private String printArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
