package nirvana.cash.loan.privilege.service.impl;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.dao.MsgListMapper;
import nirvana.cash.loan.privilege.domain.MsgList;
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
        msgList.setId(this.getSequence(MsgList.SEQ));
        msgList.setCreateTime(new Date());
        msgList.setUpdateTime(new Date());
        this.save(msgList);
        return ResResult.success();
    }
}
