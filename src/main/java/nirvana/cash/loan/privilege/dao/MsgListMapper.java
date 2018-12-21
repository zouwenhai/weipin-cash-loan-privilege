package nirvana.cash.loan.privilege.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.MsgList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */
public interface MsgListMapper  extends MyMapper<MsgList> {

    List<MsgList> findPageList(MsgList msgList);

    /**
     * 对指定订单的某个状态的消息标记为已读
     *
     * @param orderId     订单号
     * @param orderStatus 订单状态
     */
    void maskAsRead(@Param("orderId") String orderId, @Param("orderStatus") String orderStatus);

    /**
     * 查询指定订单的某个状态下的消息总数
     *
     * @param orderId     订单号
     * @param orderStatus 订单状态
     * @return
     */
    int selectCountByOrderIdAndStatus(@Param("orderId") String orderId, @Param("orderStatus") String orderStatus);
}
