package nirvana.cash.loan.privilege.controller.springmvc;

import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/11/8.
 */
@RestController
@RequestMapping("/privilige")
public class MsgController extends BaseController{

    @Autowired
    private MessageConfigService messageConfigService;
    @Autowired
    private MsgListService msgListService;

    //判断是否为站内消息发送目标对象
    @RequestMapping(value = "/isWebsocketUser")
    public ResResult isLogin(ServerHttpRequest request) {
        User user = this.getLoginUser(request);
        Long userId = user.getUserId();
        boolean flag = messageConfigService.isTargtUser(userId);
        if(flag){
            Integer count = msgListService.countUnReadMsg(userId);
            return ResResult.success(count);
        }
        return ResResult.error();
    }
}
