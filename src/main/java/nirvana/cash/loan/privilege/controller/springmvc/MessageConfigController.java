package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

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
     * @param messageConfig
     * @return
     */
    @PostMapping("/insertMessageConfig")
    public ResResult insertMessageConfig(ServerHttpRequest request, @RequestBody MessageConfig messageConfig) {
        User loginUser = getLoginUser(request);
        String username = loginUser.getUsername();
        return messageConfigService.insertMessageConfig(messageConfig, username);
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
     * @param messageConfig
     * @return
     */
    @PostMapping("/updateMessageConfig")
    public ResResult updateMessageConfig(ServerHttpRequest request, @RequestBody MessageConfig messageConfig) {
        User loginUser = getLoginUser(request);
        String username = loginUser.getUsername();
        return messageConfigService.updateMessageConfig(messageConfig, username);
    }

    /**
     * 回显
     *
     * @param configId
     * @return
     */
    @RequestMapping("/getMessageConfig")
    public ResResult getMessageConfig(@RequestParam("configId") Long configId) {
        return messageConfigService.getMessageConfig(configId);
    }

    @RequestMapping("/updateRun")
    public ResResult updateRun(ServerHttpRequest request, MessageConfig messageConfig) {
        User loginUser = getLoginUser(request);
        String username = loginUser.getUsername();
        return messageConfigService.updateRun(messageConfig, username);
    }

    @RequestMapping("/queryMsgModule")
    public ResResult queryMsgModule() {
        return messageConfigService.queryMsgModule();
    }

}
