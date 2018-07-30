package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.system.domain.SysLog;
import nirvana.cash.loan.privilege.system.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/privilige")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	@RequestMapping("log/list")
	@ResponseBody
	public Map<String, Object> logList(QueryRequest request, SysLog log) {
		PageHelper.startPage(request.getPageNum(), request.getPageSize());
		List<SysLog> list = this.logService.findAllLogs(log);
		PageInfo<SysLog> pageInfo = new PageInfo<>(list);
		return getDataTable(pageInfo);
	}

	@RequestMapping("log/excel")
	@ResponseBody
	public ResponseBo logExcel(SysLog log) {
		try {
			List<SysLog> list = this.logService.findAllLogs(log);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Excel失败，请联系网站管理员！");
		}
	}

	@RequestMapping("log/csv")
	@ResponseBody
	public ResponseBo logCsv(SysLog log){
		try {
			List<SysLog> list = this.logService.findAllLogs(log);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Csv失败，请联系网站管理员！");
		}
	}
	
	@RequestMapping("log/delete")
	@ResponseBody
	public ResponseBo deleteLogss(String ids) {
		try {
			this.logService.deleteLogs(ids);
			return ResponseBo.ok("删除日志成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除日志失败，请联系网站管理员！");
		}
	}
}
