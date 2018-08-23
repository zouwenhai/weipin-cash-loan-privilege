package nirvana.cash.loan.privilege.fegin.facade;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */
public class RiskUserAddApiFacade implements Serializable {
    private String loginName;
    private String userName;
    private String mobile;
    private String roleType;
    private String userStatus; //是否下线 0：删除 1：在线 2：下线

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

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
