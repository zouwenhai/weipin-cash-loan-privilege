package nirvana.cash.loan.privilege.domain.vo;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TB_YOFISHDK_OPTION_LOG")
public class TbYofishdkOptionLogDto {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "select SEQ_YOFISHDK_OPTION_LOG.nextval from dual")
    private Long id;

    /**
     * 姓名
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 操作Url
     */
    @Column(name = "OPTION_URL")
    private String optionUrl;

    /**
     * 操作记录
     */
    @Column(name = "OPTION_DESC")
    private String optionDesc;

    /**
     * 参数
     */
    @Column(name = "PARAMS")
    private String params;

    /**
     * 操作IP
     */
    @Column(name = "OPTION_IP")
    private String optionIp;

    /**
     * 操作时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 获取主键
     *
     * @return ID - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取姓名
     *
     * @return USERNAME - 姓名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置姓名
     *
     * @param username 姓名
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * 获取操作Url
     *
     * @return OPTION_URL - 操作Url
     */
    public String getOptionUrl() {
        return optionUrl;
    }

    /**
     * 设置操作Url
     *
     * @param optionUrl 操作Url
     */
    public void setOptionUrl(String optionUrl) {
        this.optionUrl = optionUrl == null ? null : optionUrl.trim();
    }

    /**
     * 获取操作记录
     *
     * @return OPTION_DESC - 操作记录
     */
    public String getOptionDesc() {
        return optionDesc;
    }

    /**
     * 设置操作记录
     *
     * @param optionDesc 操作记录
     */
    public void setOptionDesc(String optionDesc) {
        this.optionDesc = optionDesc == null ? null : optionDesc.trim();
    }

    /**
     * 获取参数
     *
     * @return PARAMS - 参数
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    /**
     * 获取操作IP
     *
     * @return OPTION_IP - 操作IP
     */
    public String getOptionIp() {
        return optionIp;
    }

    /**
     * 设置操作IP
     *
     * @param optionIp 操作IP
     */
    public void setOptionIp(String optionIp) {
        this.optionIp = optionIp == null ? null : optionIp.trim();
    }

    /**
     * 获取操作时间
     *
     * @return CREATE_TIME - 操作时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置操作时间
     *
     * @param createTime 操作时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}