package nirvana.cash.loan.privilege.system.service;

import java.util.List;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.Dict;

public interface DictService extends IService<Dict> {

	List<Dict> findAllDicts(Dict dict);

	Dict findById(Long dictId);

	void addDict(Dict dict);

	void deleteDicts(String dictIds);

	void updateDict(Dict dicdt);
}
