package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.dao.MessageConfigMapper;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunyong on 2018-11-05.
 */
@Service
@Slf4j
public class MessageConfigServiceImpl extends BaseService<MessageConfig> implements MessageConfigService {

    @Autowired
    private MessageConfigMapper messageConfigMapper;

    /**
     * 查询消息列表
     *
     * @return
     */
    @Override
    public List<MessageConfig> queryMessageConfigs() {
        try {
            List<MessageConfig> messageConfigs = messageConfigMapper.selectAll();
            return messageConfigs;
        } catch (Exception e) {
            log.error("消息列表查询失败:{}", e, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 新增消息列表
     *
     * @param messageConfig
     * @return
     */
    @Override
    public ResResult insertMessageConfig(MessageConfig messageConfig, String username) {
        try {
            messageConfig.setCreateUser(username);
            messageConfig.setUpdateUser(username);
            messageConfig.setCreateTime(new Date());
            messageConfig.setUpdateTime(new Date());
            messageConfig.setId(this.getSequence(MessageConfig.SEQ));
            log.info("========="+JSON.toJSONString(messageConfig));
            int i = messageConfigMapper.insertSelective(messageConfig);
            if (i > 0) {
                return ResResult.success();
            }
        } catch (Exception e) {
            log.error("消息列表新增失败:{}", e, e.getMessage());
        }
        return ResResult.error();
    }

    /**
     * 编辑消息列表
     *
     * @param messageConfig
     * @return
     */
    @Override
    public ResResult updateMessageConfig(MessageConfig messageConfig, String username) {
        try {
            messageConfig.setUpdateTime(new Date());
            messageConfig.setUpdateUser(username);
            int i = messageConfigMapper.updateMessageConfig(messageConfig);
            if (i > 0) {
                return ResResult.success();
            }
            return ResResult.error();
        } catch (Exception e) {
            log.error("消息列表编辑失败:{}", e, e.getMessage());
        }
        return ResResult.error();
    }

    /**
     * 回显
     *
     * @param configId
     * @return
     */
    @Override
    public ResResult getMessageConfig(Long configId) {
        try {
            MessageConfig messageConfig = messageConfigMapper.selectByPrimaryKey(configId);
            return ResResult.success(messageConfig);
        } catch (Exception e) {
            log.error("通知管理列表回显失败：{}", e);
        }
        return ResResult.error();
    }


    /**
     *
     * @return
     */
    @Override
    public ResResult updateRun(MessageConfig messageConfig, String username) {
        try {
            messageConfig.setUpdateUser(username);
            messageConfig.setUpdateTime(new Date());
            int i = messageConfigMapper.updateRun(messageConfig);
            if(i > 0){
                return ResResult.success();
            }
        } catch (Exception e) {
        }
        return ResResult.error();
    }

    /**
     * 删除消息列表
     *
     * @param configId
     * @return
     */
    @Override
    public ResResult delMessageConfig(Long configId) {
        try {
            messageConfigMapper.deleteByPrimaryKey(configId);
            return ResResult.success();
        } catch (Exception e) {
            log.error("消息列表删除失败:{}", e, e.getMessage());
        }
        return ResResult.error();
    }


}
