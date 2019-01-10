package nirvana.cash.loan.privilege.common.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

/**
 * Created by Administrator on 2018/11/3.
 */
public class CookieUtil {

    public static String getCookieValue(ServerHttpRequest request, String cookieName) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (cookies == null || cookies.size() == 0 || cookies.getFirst(cookieName) == null) {
            return null;
        }
        HttpCookie httpCookie = cookies.getFirst(cookieName);
        if(StringUtils.isBlank(httpCookie.getValue())){
            return null;
        }
        return URLUtil.decode(httpCookie.getValue(),"utf-8");
    }

    //会话级cookie，关闭浏览器失效
    public static ResponseCookie buildCookie(String cookieName,String cookieValue){
        return buildCookie(cookieName,cookieValue,-1);
    }

    //maxAge = 0  不记录cookie
    //maxAge = -1 会话级cookie，关闭浏览器失效
    //maxAge = 60*60 过期时间为1小时
    public static ResponseCookie buildCookie(String cookieName,String cookieValue,long maxAge){
        return ResponseCookie.from(cookieName, URLUtil.encode(cookieValue,"utf-8"))
                .domain("caiyi.com ,youyuwo.com")
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
    }
}
