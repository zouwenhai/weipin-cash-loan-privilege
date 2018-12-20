package nirvana.cash.loan.privilege.controller.springmvc;

import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.fegin.facade.CashLoanGetAllProductsFacade;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class ProductController {

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
    public ResResult deptList(Dept dept) {
        List<Dept> list = deptService.findAllDepts(dept);
        return ResResult.success(list);
    }
}
