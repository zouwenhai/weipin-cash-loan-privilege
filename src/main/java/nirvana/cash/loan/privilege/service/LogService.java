package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.domain.SysLog;

import java.util.List;

public interface LogService extends IService<SysLog> {
	
	List<SysLog> findAllLogs(SysLog log);
	
	void deleteLogs(String logIds);

	void addLog(String username,String url,long execTime,String params);
}
