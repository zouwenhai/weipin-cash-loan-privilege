package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
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

    //消息删除
    void msgDelete(List<Long> idList,User user);

    //更新阅读状态
    void updateStatus(List<Long> idList,Integer status,User user);

    //查询用户未读消息数量
    Integer countUnReadMsg(Long userId);
}
