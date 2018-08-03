package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.SysLog;
import nirvana.cash.loan.privilege.system.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/privilige")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	//日志列表
	@RequestMapping("log/list")
	@ResponseBody
	public ResResult logList(QueryRequest request, SysLog log) {
		PageHelper.startPage(request.getPageNum(), request.getPageSize());
		List<SysLog> list = this.logService.findAllLogs(log);
		PageInfo<SysLog> pageInfo = new PageInfo<>(list);
		return ResResult.success(getDataTable(pageInfo));
	}

	//删除日志
	@RequestMapping("log/delete")
	@ResponseBody
	public ResResult deleteLogss(String ids) {
		try {
			this.logService.deleteLogs(ids);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("日志管理|删除日志|执行异常:{}",e);
			return ResResult.error("删除日志失败！");
		}
	}
}
