package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MessageConfigVo;
import nirvana.cash.loan.privilege.service.base.IService;

import java.util.List;

/**
 * Created by sunyong on 2018-11-05.
 */
public interface MessageConfigService extends IService<MessageConfig> {
    List<MessageConfig> queryMessageConfigs();

    ResResult insertMessageConfig(MessageConfigVo messageConfigVo, User loginUser);

    ResResult delMessageConfig(Long configId);

    ResResult updateMessageConfig(MessageConfigVo messageConfigVo, User loginUser);
}
