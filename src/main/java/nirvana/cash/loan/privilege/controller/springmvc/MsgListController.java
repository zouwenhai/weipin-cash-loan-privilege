package nirvana.cash.loan.privilege.controller.springmvc;

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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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
    public ResResult msgList(ServerHttpRequest request, @RequestBody QueryRequest queryRequest) {
        User user = this.getLoginUser(request);
        MsgList msgList = new MsgList();
        msgList.setUserId(user.getUserId());
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

    /**
     * 查询用户未读的消息数量
     * @param request
     * @return
     */
    @GetMapping("msg/unreadCount")
    public ResResult unreadCount(ServerHttpRequest request){
        User user = this.getLoginUser(request);
        Integer count = msgListService.countUnReadMsg(user.getUserId());
        return ResResult.success(count != null ? count : 0);
    }

    /**
     * 查询用户未读的消息
     * @param request
     * @return
     */
    @GetMapping("msg/unreadMessage")
    public ResResult unreadMessage(ServerHttpRequest request){
        User user = this.getLoginUser(request);
        List<MsgList> messages = msgListService.queryUnreadMessage(user.getUserId());
        return ResResult.success(messages);
    }

}
