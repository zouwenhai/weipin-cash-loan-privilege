package nirvana.cash.loan.privilege.common.util;

import java.util.HashMap;
import java.util.Map;

public class ResResult {
    //成功
    public static final String SUCCESS = "1";
    //失败
    public static final String ERROR = "0";
    //未授权
    public static final String UNAUTHORIZED_URL = "403";
    //登录session失效
    public static final String LOGIN_SESSION_TIMEOUT = "-1";
    //账户被锁定
    public static final String ACCOUNT_LOCKED = "-2";

    //默认成功
    private String code = SUCCESS;

    private Object data;

    private String desc = "success";

    private Map<String,Object> other=new HashMap();

    public static ResResult success() {
        ResResult res = new ResResult();
        return res;
    }

    public static ResResult success(Object data) {
        ResResult res = new ResResult();
        res.setData(data);
        return res;
    }

    public static ResResult success(Object data, String desc) {
        ResResult res = new ResResult();
        res.setData(data);
        res.setDesc(desc);
        return res;
    }

    public static ResResult success(Object data, String desc, String code) {
        ResResult res = new ResResult();
        res.setData(data);
        res.setDesc(desc);
        res.setCode(code);
        return res;
    }

    public static ResResult success(String desc, String code) {
        ResResult res = new ResResult();
        res.setDesc(desc);
        res.setCode(code);
        return res;
    }

    public static ResResult error() {
        ResResult res = new ResResult();
        res.setDesc(null);
        res.setCode(ERROR);
        return res;
    }

    public static ResResult error(String desc) {
        ResResult res = new ResResult();
        res.setDesc(desc);
        res.setCode(ERROR);
        return res;
    }

    public static ResResult error(String desc, String code) {
        ResResult res = new ResResult();
        res.setDesc(desc);
        res.setCode(code);
        return res;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, Object> getOther() {
        return other;
    }

    public void setOther(Map<String, Object> other) {
        this.other = other;
    }
}
