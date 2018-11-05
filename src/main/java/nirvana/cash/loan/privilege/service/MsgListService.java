package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.service.base.IService;

import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */
public interface MsgListService extends IService<MsgList> {

    //分页列表
    List<MsgList> findPageList(MsgList msgList);

    //保存消息
    ResResult saveMsg(MsgList msgList);
}
