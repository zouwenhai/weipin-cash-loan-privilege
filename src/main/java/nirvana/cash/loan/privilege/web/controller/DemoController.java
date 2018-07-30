package nirvana.cash.loan.privilege.web.controller;

import nirvana.cash.loan.privilege.common.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/7/23.
 */
@RestController
@RequestMapping("/privilige")
public class DemoController extends BaseController {

    @RequestMapping("/gatewate/zullTest")
    public String zullTest() {
        return "nirvana.cash.loan.privilege:ZullTest";
    }

}
