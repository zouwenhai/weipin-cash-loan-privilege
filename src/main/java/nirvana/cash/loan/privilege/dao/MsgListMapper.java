package nirvana.cash.loan.privilege.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.MsgList;

import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */
public interface MsgListMapper  extends MyMapper<MsgList> {

    List<MsgList> findPageList(MsgList msgList);
}
