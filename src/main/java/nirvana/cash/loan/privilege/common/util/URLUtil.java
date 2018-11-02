package nirvana.cash.loan.privilege.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/10/30.
 */
@Slf4j
public class URLUtil {

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
}
