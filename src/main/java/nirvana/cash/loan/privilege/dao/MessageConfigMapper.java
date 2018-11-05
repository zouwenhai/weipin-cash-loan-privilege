package nirvana.cash.loan.privilege.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.MessageConfig;

import java.util.List;

/**
 * Created by sunyong on 2018-11-05.
 */
public interface MessageConfigMapper extends MyMapper<MessageConfig> {


    void insertMessageConfig(MessageConfig messageConfig);
}
