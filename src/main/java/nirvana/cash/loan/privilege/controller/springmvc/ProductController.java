package nirvana.cash.loan.privilege.controller.springmvc;

import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.fegin.facade.CashLoanGetAllProductsFacade;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/privilige")
public class ProductController extends BaseController {

    @Autowired
    private DeptProductService deptProductService;
    @Autowired
    private DeptService deptService;

    /**
     * 查询全部产品列表
     *
     * @return
     */
    @GetMapping(value = "/notauth/findAllProductList")
    public ResResult getAllProductList() {
        List<CashLoanGetAllProductsFacade> list = deptProductService.findAllProductList();
        List<Map> listmap = new ArrayList<>();
        list.stream().forEach(t -> {
            Map item = new HashMap();
            item.put("productNo", t.getShowId());
            item.put("productName", t.getName());
            listmap.add(item);
        });
        return ResResult.success(listmap);
    }

    //全部部门列表
    @RequestMapping("notauth/allDept")
    public ResResult deptList(@RequestHeader String authDeptId) {
        if(CommonContants.default_dept_id.equals(authDeptId)){
            return ResResult.success(new ArrayList<>());
        }
        Dept dept =deptService.findById(Long.valueOf(authDeptId));
        List<Dept> list = deptService.findAllDepts(new Dept());
        if(dept.getViewRange() == 1){
            list = list.stream().filter(t->t.getDeptId().equals(authDeptId)).collect(Collectors.toList());
        }
        return ResResult.success(list);
    }
}
