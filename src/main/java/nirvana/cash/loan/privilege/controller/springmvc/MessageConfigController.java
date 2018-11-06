package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MessageConfigVo;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by sunyong on 2018-11-05.
 * 消息通知列表
 */
@RestController
@RequestMapping("/privilige")
public class MessageConfigController extends BaseController {

    @Autowired
    private MessageConfigService messageConfigService;

    /**
     * 查询消息列表
     *
     * @return
     */
    @RequestMapping("/queryMessageConfigs")
    public ResResult queryMessageConfigs(QueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<MessageConfig> messageConfigList = messageConfigService.queryMessageConfigs();
        PageInfo pageInfo = new PageInfo<>(messageConfigList);
        return ResResult.success(pageInfo);
    }

    /**
     * 新增消息列表
     *
     * @param messageConfigVo
     * @return
     */
    @RequestMapping("/insertMessageConfig")
    public ResResult insertMessageConfig(ServerHttpRequest request, MessageConfigVo messageConfigVo) {
        User loginUser = getLoginUser(request);
        return messageConfigService.insertMessageConfig(messageConfigVo, loginUser);
    }

    /**
     * 删除消息列表
     *
     * @param configId
     * @return
     */
    @RequestMapping("/delMessageConfig")
    public ResResult delMessageConfig(@RequestParam("configId") Long configId) {

        return messageConfigService.delMessageConfig(configId);
    }

    /**
     * 编辑消息列表
     *
     * @param messageConfigVo
     * @return
     */
    @RequestMapping("/updateMessageConfig")
    public ResResult updateMessageConfig(ServerHttpRequest request, MessageConfigVo messageConfigVo) {
        User loginUser = getLoginUser(request);
        return messageConfigService.updateMessageConfig(messageConfigVo, loginUser);
    }

    /**
     * 回显
     * @param configId
     * @return
     */
    @RequestMapping("/getMessageConfig")
    public ResResult getMessageConfig(@RequestParam("configId") Long configId){
        return messageConfigService.getMessageConfig(configId);
    }
}
