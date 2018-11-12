package nirvana.cash.loan.privilege.controller.springmvc;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MsgListDeleteVo;
import nirvana.cash.loan.privilege.domain.vo.MsgListReadVo;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMessageFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/11/5.
 */
@RestController
@RequestMapping("/privilige")
public class MsgListController extends BaseController {

    @Autowired
    private MsgListService msgListService;

    //消息列表
    @PostMapping("msg/list")
    public ResResult msgList(@RequestBody QueryRequest queryRequest) {
        MsgList msgList = new MsgList();
        PageHelper.startPage(queryRequest.getPageNum(), queryRequest.getPageSize());
        List<MsgList> list = msgListService.findPageList(msgList);
        PageInfo<MsgList> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

    //批量消息删除
    @PostMapping("msg/delete")
    public ResResult msgDelete(ServerHttpRequest request, @RequestBody MsgListDeleteVo vo) {
        String ids = vo.getIds();
        if (StringUtils.isBlank(ids)) {
            return ResResult.error("消息ID不存在");
        }
        User user = this.getLoginUser(request);
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(t -> Long.valueOf(t))
                .collect(Collectors.toList());
        msgListService.msgDelete(idList, user);
        return ResResult.success();
    }

    //批量消息已读
    @PostMapping("msg/batchRead")
    public ResResult batchRead(ServerHttpRequest request, @RequestBody MsgListReadVo vo) {
        String ids = vo.getIds();
        if (StringUtils.isBlank(ids)) {
            return ResResult.error("消息ID不存在");
        }
        User user = this.getLoginUser(request);
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(t -> Long.valueOf(t))
                .collect(Collectors.toList());
        msgListService.updateStatus(idList, 1, user);
        return ResResult.success();
    }

    /**
     * 设置消息状态为已读
     * @param request
     * @return
     */
    @GetMapping("msg/read")
    public ResResult msgRead(ServerHttpRequest request,@RequestParam(name="uuid") String uuid){
        User user = this.getLoginUser(request);
        msgListService.updateMessageStatus(uuid,1,user);
        return ResResult.success();
    }

    @Autowired
    private AmqpTemplate rabbit;

    @GetMapping("/notauth/testWebSocket/{userId}")
    public ResResult testWebSocket(@PathVariable Long userId){

        WebSocketMessageFacade facade = new WebSocketMessageFacade();
        facade.setUserId(userId);
        facade.setUuid(UUID.randomUUID().toString());
        facade.setMsg("消息内容1......");
        rabbit.convertAndSend("exchange_auth_msg_notice_websocket","routingkey_auth_msg_notice_websocket", JSON.toJSONString(facade));

        WebSocketMessageFacade facade2 = new WebSocketMessageFacade();
        facade2.setUserId(userId);
        facade2.setUuid(UUID.randomUUID().toString());
        facade2.setMsg("消息内容2......");
        rabbit.convertAndSend("exchange_auth_msg_notice_websocket","routingkey_auth_msg_notice_websocket",JSON.toJSONString(facade2));
        return ResResult.success();
    }

}
