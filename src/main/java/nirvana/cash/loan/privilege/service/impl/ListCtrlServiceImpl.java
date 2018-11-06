package nirvana.cash.loan.privilege.service.impl;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.service.ListCtrlService;
import nirvana.cash.loan.privilege.dao.ListCtrlMapper;
import nirvana.cash.loan.privilege.domain.ListCtrl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/2.
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ListCtrlServiceImpl extends BaseService<ListCtrl> implements ListCtrlService {

    @Autowired
    private ListCtrlMapper listCtrlMapper;

    @Override
    public ListCtrl findListCtrl(Long userId, Long menuId) {
        Example example = new Example(ListCtrl.class);
        example.createCriteria().andEqualTo("userId",userId)
                .andEqualTo("menuId",menuId);
       return this.selectOneByExample(example);
    }

    @Transactional
    @Override
    public ResResult saveOrUpdate(ListCtrl listCtrl) {
        Long userId = listCtrl.getUserId();
        Long menuId = listCtrl.getMenuId();
        String newHiddenColumn =  listCtrl.getHiddenColumn();
        ListCtrl oldListCtrl = this.findListCtrl(userId,menuId);
        if (oldListCtrl == null) {
            listCtrl.setId(this.getSequence(ListCtrl.SEQ));
            listCtrl.setCreateTime(new Date());
            listCtrl.setUpdateTime(new Date());
            this.save(listCtrl);
            return ResResult.success();
        }

        BeanUtils.copyProperties(oldListCtrl,listCtrl);
        listCtrl.setUpdateTime(new Date());
        listCtrl.setHiddenColumn(newHiddenColumn);
        Example example = new Example(ListCtrl.class);
        example.createCriteria().andEqualTo("id",oldListCtrl.getId());
        listCtrlMapper.updateByExample(listCtrl,example);
        return ResResult.success();
    }
}
