package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.ListCtrl;

/**
 * Created by Administrator on 2018/11/2.
 */
public interface ListCtrlService extends IService<ListCtrl> {

    ListCtrl findListCtrl(Long userId,Long menuId);

    ResResult saveOrUpdate(ListCtrl listCtrl);



}
