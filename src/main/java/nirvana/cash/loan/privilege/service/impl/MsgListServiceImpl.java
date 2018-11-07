package nirvana.cash.loan.privilege.service.impl;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.dao.MsgListMapper;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */
@Slf4j
@Service
public class MsgListServiceImpl extends BaseService<MsgList> implements MsgListService {

    @Autowired
    private MsgListMapper msgListMapper;

    @Override
    public List<MsgList> findPageList(MsgList msgList) {
        return msgListMapper.findPageList(msgList);
    }

    @Override
    public ResResult saveMsg(MsgList msgList) {
        String uuid = msgList.getUuid();
        Example example = new Example(MsgList.class);
        example.createCriteria().andEqualTo("uuid",uuid);
        int rows = msgListMapper.selectCountByExample(example);
        if(rows > 0){
            log.info("重复消息:msg uuid={}",uuid);
            return ResResult.error("重复消息");
        }
        msgList.setIsDelete(0);
        msgList.setId(this.getSequence(MsgList.SEQ));
        msgList.setCreateUser("system");
        msgList.setUpdateUser("system");
        msgList.setCreateTime(new Date());
        msgList.setUpdateTime(new Date());
        this.save(msgList);
        return ResResult.success();
    }

    @Override
    public void msgDelete(List<Long> idList,User user) {
        MsgList msgList = new MsgList();
        msgList.setUpdateUser(user.getUsername());
        msgList.setUpdateTime(new Date());

        Example example = new Example(MsgList.class);
        example.createCriteria().andIn("id",idList);
        msgListMapper.updateByExampleSelective(msgList,example);
    }

    @Override
    public MsgList msgRead(Long id) {
        return msgListMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(List<Long> idList, Integer status,User user) {
        MsgList msgList = new MsgList();
        msgList.setUpdateUser(user.getUsername());
        msgList.setUpdateTime(new Date());
        msgList.setStatus(status);

        Example example = new Example(MsgList.class);
        example.createCriteria().andIn("id",idList);
        msgListMapper.updateByExampleSelective(msgList,example);

    }

    @Override
    public Integer countUnReadMsg(Long userId) {
        Example example = new Example(MsgList.class);
        example.createCriteria().andEqualTo("userId",userId);
        return msgListMapper.selectCountByExample(example);
    }
}