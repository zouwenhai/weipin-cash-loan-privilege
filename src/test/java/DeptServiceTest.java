import nirvana.cash.loan.privilege.common.dao.SeqenceMapper;
import nirvana.cash.loan.privilege.system.domain.Dept;
import nirvana.cash.loan.privilege.system.service.DeptService;
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
        dept.setDeptName("test-0001");
        deptService.addDept(dept);
        System.err.println("done");
    }

    @Test
    public void getSequence(){
        Long id = seqenceMapper.getSequence("seq_tb_yofishdk_auth_dept");
        System.err.println("id="+id);
    }
}
