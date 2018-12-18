import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.domain.DeptProduct;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.fegin.FeginCashLoanApi;
import nirvana.cash.loan.privilege.fegin.NewResponseUtil;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.Transient;
import java.util.List;

public class v5Test extends BaseTest {
    @Autowired
    private FeginCashLoanApi feginCashLoanApi;
    @Autowired
    private UserService userService;
    @Autowired
    private DeptProductService deptProductService;

    @Test
    public void findUserWithDept() {
        List<User> list = this.userService.findUserWithDept(new User());
        System.err.println(JSON.toJSONString(list));
    }

    @Test
    public void getAllProductList(){
        NewResponseUtil responseUtil = feginCashLoanApi.getAllProductList();
        System.err.println(JSON.toJSONString(responseUtil));
    }

    @Test
    public void findListByDeptIds(){
        Long deptId = 144L;
        String productNos = deptProductService.findProductNosByDeptId(deptId);
        System.err.println(productNos);
    }

    @Test
    public void delete(){
        Long deptId = 142L;
        deptProductService.delete(deptId);
    }

}
