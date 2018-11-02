package nirvana.cash.loan.privilege.system.controller;

import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.ListCtrl;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.ListCtrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 列表隐藏列配置
 * Created by Administrator on 2018/11/2.
 */
@Controller
@RequestMapping("/privilige")
public class ListCtrlController extends BaseController {

    @Autowired
    private ListCtrlService listCtrlService;

    //列表隐藏列配置接口
    @PostMapping("/listCtrl/saveOrUpdate")
    public ResResult saveOrUpdate(HttpServletRequest request, @RequestBody ListCtrl listCtrl) {
        User user = this.getLoginUser(request);
        listCtrl.setUserId(user.getUserId());
        return listCtrlService.saveOrUpdate(listCtrl);
    }
}
