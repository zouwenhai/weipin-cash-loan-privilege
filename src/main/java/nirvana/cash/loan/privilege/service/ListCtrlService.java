package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.ListCtrl;

/**
 * Created by Administrator on 2018/11/2.
 */
public interface ListCtrlService extends IService<ListCtrl> {

    ListCtrl findListCtrl(Long userId,Long menuId);

    ResResult saveOrUpdate(ListCtrl listCtrl);



}
