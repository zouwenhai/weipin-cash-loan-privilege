package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResResult userList(QueryRequest queryRequest, MsgList msgList) {
        PageHelper.startPage(queryRequest.getPageNum(), queryRequest.getPageSize());
        List<MsgList> list = msgListService.findPageList(msgList);
        PageInfo<MsgList> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

}
