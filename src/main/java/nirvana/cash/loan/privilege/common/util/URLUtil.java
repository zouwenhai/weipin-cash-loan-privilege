package nirvana.cash.loan.privilege.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Administrator on 2018/10/30.
 */
@Slf4j
public class URLUtil {

    //queryString: a=1&b=2&c=3
    public static String getValue(String queryString, String paramName) {
        if(StringUtils.isBlank(queryString)){
            return null;
        }
        String[] params = queryString.split("&");
        for (int i = 0; i < params.length; i++) {
            if(!params[i].contains("=")){
                continue;
            }
            String[] pair = params[i].split("=");
            if (paramName.equals(pair[0]) && pair.length == 2) {
                return pair[1];
            }
        }
        return null;
    }

    public static String encode(String str, String enc) {
        try {
            return URLEncoder.encode(str, enc);
        } catch (UnsupportedEncodingException e) {
            log.info("参数编码格式错误:str={},enc={}", str, enc);
        }
        return str;
    }

    public static String decode(String str, String enc) {
        try {
            return URLDecoder.decode(str, enc);
        } catch (UnsupportedEncodingException e) {
            log.info("参数编码格式错误:str={},enc={}", str, enc);
        }
        return str;
    }


    public static boolean isEndsWith(List<String> urls, String url){
        for (String item : urls) {
            if(item.endsWith(url)){
                return true;
            }
        }
        return false;
    }

    public static boolean isInWhiteList(List<String> urls,String url) {
        PathMatcher pathMatcher = new AntPathMatcher();
        return urls.stream().anyMatch(white -> pathMatcher.match(white, url));
    }

    public static boolean isWebsocketUrl(String websocketUrl,String url) {
        PathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match(websocketUrl, url);
    }
}
