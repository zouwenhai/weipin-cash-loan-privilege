package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.system.domain.Dict;
import nirvana.cash.loan.privilege.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class DictController extends BaseController {

	@Autowired
	private DictService dictService;

	@RequestMapping("dict/list")
	public Map<String, Object> dictList(QueryRequest request, Dict dict) {
		PageHelper.startPage(request.getPageNum(), request.getPageSize());
		List<Dict> list = this.dictService.findAllDicts(dict);
		PageInfo<Dict> pageInfo = new PageInfo<>(list);
		return getDataTable(pageInfo);
	}

	@RequestMapping("dict/excel")
	public ResponseBo dictExcel(Dict dict) {
		try {
			List<Dict> list = this.dictService.findAllDicts(dict);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Excel失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dict/csv")
	public ResponseBo dictCsv(Dict dict){
		try {
			List<Dict> list = this.dictService.findAllDicts(dict);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Csv失败，请联系网站管理员！");
		}
	}
	
	@RequestMapping("dict/getDict")
	public ResponseBo getDict(Long dictId) {
		try {
			Dict dict = this.dictService.findById(dictId);
			return ResponseBo.ok(dict);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取字典信息失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dict/add")
	public ResponseBo addDict(Dict dict) {
		try {
			this.dictService.addDict(dict);
			return ResponseBo.ok("新增字典成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增字典失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dict/delete")
	public ResponseBo deleteDicts(String ids) {
		try {
			this.dictService.deleteDicts(ids);
			return ResponseBo.ok("删除字典成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除字典失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dict/update")
	public ResponseBo updateDict(Dict dict) {
		try {
			this.dictService.updateDict(dict);
			return ResponseBo.ok("修改字典成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改字典失败，请联系网站管理员！");
		}
	}
}
