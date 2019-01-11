package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.domain.CacheDto;
import nirvana.cash.loan.privilege.service.base.IService;

public interface AuthCacheService extends IService<CacheDto> {

    CacheDto findOne(String key);

    void insert(String key,String value,String remark);

}
