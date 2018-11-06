package nirvana.cash.loan.privilege.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

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

    public  static  byte[] json2Bytes(JSONObject data){
        try {
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            oos.writeObject(data);
            byte[] resbytes = bos.toByteArray();
            oos.close();
            bos.close();
            return resbytes;
        } catch (Exception ex) {
            log.error("JSONObject转字节数组异常:str={},exception={}",data.toJSONString(),ex);
        }
        return null;
    }

    public static JSONObject bytes2json(byte[] bytes){
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream sIn = new ObjectInputStream(in);
            Object obj =  sIn.readObject();
            sIn.close();
            in.close();
            return JSONObject.parseObject(JSON.toJSONString(obj));
        } catch (Exception ex) {
            log.error("字节数组转JSONObject对象异常:exception={}",ex);
        }
        return JSONObject.parseObject(JSON.toJSONString(ResResult.error("系统异常")));

    }
}
