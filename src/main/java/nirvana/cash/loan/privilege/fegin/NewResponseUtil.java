package nirvana.cash.loan.privilege.fegin;

import java.io.Serializable;

public class NewResponseUtil<T> implements Serializable {

    public static final String SUCCESS = "1";
    public static final String FAILED = "0";
    public static final String ERROR = "500";
    public static final String SUCCEED = "success";
    public static final String FAILURE = "error";

    private String code;
    private T data;
    private String desc;

    public NewResponseUtil() {
    }

    public NewResponseUtil(String code, T data, String desc) {
        this.code = code;
        this.data = data;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
