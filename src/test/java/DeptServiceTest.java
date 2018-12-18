import nirvana.cash.loan.privilege.dao.base.SeqenceMapper;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.service.DeptService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2018/10/23.
 */
public class DeptServiceTest extends BaseTest {

    @Autowired
    private DeptService  deptService;

    @Autowired
    private SeqenceMapper seqenceMapper;

    @Test
    public void addDept(){
        Long id = seqenceMapper.getSequence("seq_tb_yofishdk_auth_dept");
        System.err.println("id="+id);

        Dept dept = new Dept();
        dept.setDeptId(id);
        dept.setParentId(0L);
        dept.setDeptName("v5-部门1");
        dept.setProductNos("1,2,3");
        deptService.addDept(dept);
        System.err.println("done");
    }

    @Test
    public void getSequence(){
        Long id = seqenceMapper.getSequence("seq_tb_yofishdk_auth_dept");
        System.err.println("id="+id);
    }
}
