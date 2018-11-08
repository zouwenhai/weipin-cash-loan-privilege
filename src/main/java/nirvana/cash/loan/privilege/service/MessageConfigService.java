package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.service.base.IService;

import java.util.List;

/**
 * Created by sunyong on 2018-11-05.
 */
public interface MessageConfigService extends IService<MessageConfig> {
    List<MessageConfig> queryMessageConfigs();

    ResResult insertMessageConfig(MessageConfig messageConfig, String username);

    ResResult delMessageConfig(Long configId);

    ResResult updateMessageConfig(MessageConfig messageConfig, String username);

    ResResult getMessageConfig(Long configId);

    ResResult updateRun(MessageConfig messageConfig,String username);

    //根据统模块，查询运行中消息配置
    MessageConfig findMessageConfigByMsgModule(Integer msgModule,long cacheTime);
}
