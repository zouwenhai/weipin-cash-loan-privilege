package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.vo.MsgListDeleteVo;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping("msg/list")
    public ResResult msgList(@RequestBody QueryRequest queryRequest, @RequestBody MsgList msgList) {
        PageHelper.startPage(queryRequest.getPageNum(), queryRequest.getPageSize());
        List<MsgList> list = msgListService.findPageList(msgList);
        PageInfo<MsgList> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

    //消息删除
    @RequestMapping("msg/delete")
    public ResResult msgDelete(@RequestBody MsgListDeleteVo vo) {
        String ids = vo.getIds();
        if (StringUtils.isBlank(ids)) {
            return ResResult.error("消息ID不存在");
        }
        List<Long> idList = Arrays.asList(ids.split(",")).stream().map(t -> Long.valueOf(t))
                .collect(Collectors.toList());
        msgListService.msgDelete(idList);
        return ResResult.success();
    }

}
