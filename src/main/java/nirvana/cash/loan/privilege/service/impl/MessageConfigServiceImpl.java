package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.enums.MsgChannelEnum;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.util.ListUtil;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.dao.MessageConfigMapper;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.vo.MsgConfigDetailVo;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sunyong on 2018-11-05.
 */
@Service
@Slf4j
public class MessageConfigServiceImpl extends BaseService<MessageConfig> implements MessageConfigService {

    @Autowired
    private MessageConfigMapper messageConfigMapper;
    @Autowired
    public RedisService redisService;

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
            Example example = new Example(MessageConfig.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("msgModule", messageConfig.getMsgModule());
            List<MessageConfig> messageConfigs = messageConfigMapper.selectByExample(example);
            if (messageConfigs != null && messageConfigs.size() > 0) {
                return ResResult.error("通知模块 " + MsgModuleEnum.getMsgModuleEnum(messageConfig.getMsgModule()).getName
                        () + " 不能重复添加");
            }
            messageConfig.setCreateUser(username);
            messageConfig.setUpdateUser(username);
            messageConfig.setCreateTime(new Date());
            messageConfig.setUpdateTime(new Date());
            messageConfig.setId(this.getSequence(MessageConfig.SEQ));
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
     * @return
     */
    @Override
    public ResResult updateRun(MessageConfig messageConfig, String username) {
        try {
            messageConfig.setUpdateUser(username);
            messageConfig.setUpdateTime(new Date());
            int i = messageConfigMapper.updateRun(messageConfig);
            if (i > 0) {
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

    @Override
    public MessageConfig findMessageConfigByMsgModule(Integer msgModule, long cacheTime) {
        List<MessageConfig> msgConfigs = redisService.getList(RedisKeyContant.yofishdk_msg_notice_config,
                MessageConfig.class);
        if (msgConfigs == null) {
            Example example = new Example(MessageConfig.class);
            example.createCriteria().andEqualTo("isRun", 1);
            msgConfigs = messageConfigMapper.selectByExample(example);
            if (ListUtil.isEmpty(msgConfigs)) {
                return null;
            }
            redisService.putListWithExpireTime(RedisKeyContant.yofishdk_msg_notice_config, msgConfigs, cacheTime);
        }
        msgConfigs = msgConfigs.stream().filter(t -> t.getMsgModule() == msgModule.intValue())
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(msgConfigs)) {
            return null;
        }
        return msgConfigs.get(0);
    }

    @Override
    public boolean isTargtUser(Long userId) {
        Example example = new Example(MessageConfig.class);
        example.createCriteria().andEqualTo("isRun", 1);
        List<MessageConfig> msgConfigs = messageConfigMapper.selectByExample(example);
        List<String> list = msgConfigs.stream().filter(t -> StringUtils.isNotBlank(t.getMsgContent()))
                .map(t -> t.getMsgContent())
                .collect(Collectors.toList());
        for (String msgContent : list) {
            List<MsgConfigDetailVo> voList = JSON.parseArray(msgContent, MsgConfigDetailVo.class);
            for (MsgConfigDetailVo vo : voList) {
                if (vo.getMsgTarget().contains(userId.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ResResult queryMsgModule(Integer moduleId) {
        Example example = new Example(MessageConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("msgModule", moduleId);
        List<MessageConfig> messageConfigs = messageConfigMapper.selectByExample(example);
        if (messageConfigs != null && messageConfigs.size() > 0) {
            return ResResult.error(MsgModuleEnum.getMsgModuleEnum(moduleId).getName() + "模块已创建");
        }
        return ResResult.success();
    }
}
