package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Dict;
import nirvana.cash.loan.privilege.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/privilige")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    //字典列表
    @RequestMapping("dict/list")
    public ResResult dictList(QueryRequest request, Dict dict) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<Dict> list = this.dictService.findAllDicts(dict);
        PageInfo<Dict> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

    //根据dictId，查询指定字典信息
    @RequestMapping("dict/getDict")
    public ResResult getDict(Long dictId) {
        try {
            Dict dict = this.dictService.findById(dictId);
            return ResResult.success(dict);
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("获取字典信息失败！");
        }
    }

    //新增字典
    @RequestMapping("dict/add")
    public ResResult addDict(Dict dict) {
        try {
            this.dictService.addDict(dict);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("字典管理|新增字典|执行异常:{}",e);
            return ResResult.error("新增字典失败！");
        }
    }

    //修改字典
    @RequestMapping("dict/update")
    public ResResult updateDict(Dict dict) {
        try {
            this.dictService.updateDict(dict);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("字典管理|修改字典|执行异常:{}",e);
            return ResResult.error("修改字典失败！");
        }
    }

    //删除字典
    @RequestMapping("dict/delete")
    public ResResult deleteDicts(String ids) {
        try {
            this.dictService.deleteDicts(ids);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("字典管理|删除字典|执行异常:{}",e);
            return ResResult.error("删除字典失败！");
        }
    }

}
