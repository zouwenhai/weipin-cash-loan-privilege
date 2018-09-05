package nirvana.cash.loan.privilege.common.enums;

/**
 * 角色枚举类型
 * 添加service参数，则用户将同步到子系统
 */
public enum RoleEnum {

    //超级管理员
    SUPERVISOR("supervisor", "超级管理员"),
    //系统管理员
    ADMINISTRATOR("administrator", "系统管理员"),
    //风控系统管理员
    RISK_ADMINISTRATOR("risk_administrator", "风控系统管理员"),
    //产品管理员
    PRODUCT_ADMINISTRATOR("product_administrator", "产品管理员"),
    //风控规则配置人员
    RISK_DEVELOPER("risk_developer", "风控规则配置人员"),
    //风控审核主管
    RISK_ADMIN("risk_admin", "风控审核主管", "risk"),
    //风控审核专员
    RISK_USER("risk_user", "风控审核专员", "risk"),
    //放款审核专员
    LOAN_USER("loan_user", "放款审核专员"),
    //催收系统管理员
    COLL_MANAGER("coll_supervisor", "催收系统管理员"),
    //催收主管(外部机构)
    COLL_ADMIN("coll_admin", "催收主管", "coll"),
    //催收专员(外部机构)
    COLL_USER("coll_user", "催收专员", "coll"),
    //财务专员
    FINANCIAL_USER("financial_user", "财务专员"),
    //客服角色
    CUSTOMER_USER("customer_user", "客服人员"),
    //运营角色
    OPERATION_USER("operation_user", "运营人员");


    private String code;

    private String name;

    //添加此参数，则用户需要同步到子系统
    private String service;

    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    RoleEnum(String code, String name, String service) {
        this.code = code;
        this.name = name;
        this.service = service;
    }

    public static RoleEnum getPaymentStatusEnumByValue(String code) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getCode().equals(code)) {
                return roleEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
