package nirvana.cash.loan.privilege.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/11/5.
 */
@Slf4j
public class ByteUtil {

    public  static  byte[] str2Bytes(String str,String charSet){
        try {
            return str.getBytes(charSet);
        } catch (UnsupportedEncodingException ex) {
            log.error("字符串转字节数组异常:str={},exception={}",str,ex);
        }
        return null;
    }

    public  static  byte[] json2Bytes(JSONObject data, String charSet){
        try {
            return data.getBytes(charSet);
        } catch (Exception ex) {
            log.error("字符串转字节数组异常:str={},exception={}",data.toJSONString(),ex);
        }
        return null;
    }
}
