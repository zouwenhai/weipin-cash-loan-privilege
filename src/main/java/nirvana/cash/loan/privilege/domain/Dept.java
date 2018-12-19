package nirvana.cash.loan.privilege.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import nirvana.cash.loan.privilege.common.annotation.ExportConfig;

@Data
@Table(name = "tb_yofishdk_auth_dept")
public class Dept implements Serializable {

	public static final String SEQ = "seq_tb_yofishdk_auth_dept";

	@Id
	@Column(name = "DEPT_ID")
	@ExportConfig(value = "编号")
	private Long deptId;

	@Column(name = "PARENT_ID")
	private Long parentId;

	@Column(name = "DEPT_NAME")
	@ExportConfig(value = "部门名称")
	private String deptName;

	@Column(name = "ORDER_NUM")
	private Long orderNum;

	@Column(name = "CREATE_TIME")
	@ExportConfig(value = "创建时间", convert = "c:TimeConvert")
	private Date createTime;

	@Column(name = "IS_DELETE")
	private Integer isDelete;

	@Transient
	private String productNos;

}