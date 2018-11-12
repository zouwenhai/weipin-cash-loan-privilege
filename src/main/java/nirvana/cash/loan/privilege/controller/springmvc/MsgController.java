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

import java.util.Map;
import java.util.HashMap;
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
        Map<String,Object> resMap = new HashMap();
        resMap.put("count",0);
        resMap.put("flag",0);
        User user = this.getLoginUser(request);
        Long userId = user.getUserId();
        boolean flag = messageConfigService.isTargtUser(userId);
        if(flag){
            Integer count = msgListService.countUnReadMsg(userId);
            resMap.put("count",count);
            resMap.put("flag",1);
        }
        return ResResult.success(resMap);
    }
}
