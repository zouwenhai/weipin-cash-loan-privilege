package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.vo.MessageConfigVo;
import nirvana.cash.loan.privilege.service.base.IService;

/**
 * Created by sunyong on 2018-11-05.
 */
public interface MessageConfigService extends IService<MessageConfig> {
    ResResult queryMessageConfigs();

    ResResult insertMessageConfig(MessageConfigVo messageConfigVo);

    ResResult delMessageConfig(Long configId);

    ResResult updateMessageConfig(MessageConfigVo messageConfigVo);
}
