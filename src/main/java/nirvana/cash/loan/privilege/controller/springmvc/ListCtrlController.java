package nirvana.cash.loan.privilege.controller.springmvc;

import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.ListCtrl;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.ListCtrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;


/**
 * 列表隐藏列配置
 * Created by Administrator on 2018/11/2.
 */
@RestController
@RequestMapping("/privilige")
public class ListCtrlController extends BaseController {

    @Autowired
    private ListCtrlService listCtrlService;

    //列表隐藏列配置接口
    @PostMapping("/listCtrl/saveOrUpdate")
    public ResResult saveOrUpdate(ServerHttpRequest request, @RequestBody ListCtrl listCtrl) {
        User user = this.getLoginUser(request);
        listCtrl.setUserId(user.getUserId());
        return listCtrlService.saveOrUpdate(listCtrl);
    }

    //查询列表隐藏字段
    @GetMapping("/notauth/listCtrl/findHiddenColumn")
    public ResResult findHiddenColumn(ServerHttpRequest request, @RequestParam Long menuId) {
        User user = this.getLoginUser(request);
        ListCtrl listCtrl =  listCtrlService.findListCtrl(user.getUserId(),menuId);
        String hiddenColumn =  "";
        if(listCtrl != null){
            hiddenColumn = listCtrl.getHiddenColumn();
        }
        return ResResult.success(hiddenColumn,"查询成功",ResResult.SUCCESS);
    }


}
