package nirvana.cash.loan.privilege.fegin.facade;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/2.
 */
public class UserAddApiFacade  implements Serializable {
    private String loginName;
    private String userName;
    private String mobile;
    private Integer roleType;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }
}
