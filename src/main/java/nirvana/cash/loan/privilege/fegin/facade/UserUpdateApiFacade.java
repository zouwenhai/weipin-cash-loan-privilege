package nirvana.cash.loan.privilege.fegin.facade;

import java.util.List;

/**
 * Created by Administrator on 2018/8/22.
 */
public class UserUpdateApiFacade {
    private String loginName;
    private String userName;
    private String mobile;
    private List<String> roleCodeList;
    private Integer status; // 1-修改；2-删除

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

    public List<String> getRoleCodeList() {
        return roleCodeList;
    }

    public void setRoleCodeList(List<String> roleCodeList) {
        this.roleCodeList = roleCodeList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
