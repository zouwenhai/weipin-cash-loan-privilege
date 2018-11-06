package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.dao.MessageConfigMapper;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MessageConfigVo;
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
     * @param messageConfigVo
     * @return
     */
    @Override
    public ResResult insertMessageConfig(MessageConfigVo messageConfigVo, User loginUser) {
        try {
            String msgJson = messageConfigVo.getMsgJson();
            JSONArray jsonArray = JSONArray.parseArray(msgJson);
            List<MessageConfig> list = new ArrayList<>();
            if (jsonArray != null && jsonArray.size() > 0) {
                jsonArray.forEach(json -> {
                    JSONObject jsonObject = (JSONObject) json;
                    MessageConfig messageConfig = new MessageConfig();
                    messageConfig.setMsgModule(messageConfigVo.getMsgModule());
                    messageConfig.setMsgChannel(jsonObject.getInteger("msgChannel"));
                    messageConfig.setMsgTarget(jsonObject.getString("msgTarget"));
                    messageConfig.setStartTime(jsonObject.getString("startTime"));
                    messageConfig.setEndTime(jsonObject.getString("endTime"));
                    messageConfig.setCreateUser(loginUser.getUsername());
                    messageConfig.setUpdateUser(loginUser.getUsername());
                    list.add(messageConfig);
                });
            }
            if (list != null && list.size() > 0) {
                list.forEach(messageConfig -> {
                    log.info("123");
                    messageConfigMapper.insertMessageConfig(messageConfig);
                });
            }
            return ResResult.success();
        } catch (Exception e) {
            log.error("消息列表新增失败:{}", e, e.getMessage());
            return ResResult.error();
        }
    }

    /**
     * 编辑消息列表
     *
     * @param messageConfigVo
     * @return
     */
    @Override
    public ResResult updateMessageConfig(MessageConfigVo messageConfigVo, User loginUser) {
        try {
            String msgJson = messageConfigVo.getMsgJson();
            JSONArray jsonArray = JSONArray.parseArray(msgJson);
            List<MessageConfig> list = new ArrayList<>();
            if (jsonArray != null && jsonArray.size() > 0) {
                jsonArray.forEach(json -> {
                    MessageConfig messageConfig = new MessageConfig();
                    JSONObject jsonObject = (JSONObject) json;
                    messageConfig.setId(jsonObject.getLong("id"));
                    messageConfig.setMsgModule(messageConfigVo.getMsgModule());
                    messageConfig.setMsgChannel(jsonObject.getInteger("msgChannel"));
                    messageConfig.setMsgTarget(jsonObject.getString("msgTarget"));
                    messageConfig.setStartTime(jsonObject.getString("startTime"));
                    messageConfig.setEndTime(jsonObject.getString("endTime"));
                    messageConfig.setUpdateTime(new Date());
                    messageConfig.setUpdateUser(loginUser.getUsername());
                    list.add(messageConfig);
                });
            }
            if (list != null && list.size() > 0) {
                list.forEach(messageConfig -> {
                    messageConfigMapper.updateByPrimaryKeySelective(messageConfig);
                });
            }
            return ResResult.success();
        } catch (Exception e) {
            log.error("消息列表编辑失败:{}", e, e.getMessage());
            return ResResult.error();
        }
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
            return ResResult.error();
        }
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
            return ResResult.error();
        }
    }


}
