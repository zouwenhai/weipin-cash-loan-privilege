import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.domain.ListCtrl;
import nirvana.cash.loan.privilege.mq.OrderStatusChangeReceiver;
import nirvana.cash.loan.privilege.mq.facade.OrderStatusChangeFacade;
import nirvana.cash.loan.privilege.service.ListCtrlService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2018/11/2.
 */
public class V3Test extends BaseTest {

    @Autowired
    private ListCtrlService listCtrlService;
    @Autowired
    private OrderStatusChangeReceiver orderStatusChangeReceiver;

    @Test
    public void saveOrUpdate() {
        ListCtrl listCtrl = new ListCtrl();
        listCtrl.setUserId(1L);
        listCtrl.setMenuId(4L);
        listCtrl.setHiddenColumn("a,b,c");
        listCtrlService.saveOrUpdate(listCtrl);
        System.err.println("done");
    }

    @Test
    public void findListCtrl() {
        Long userId = 1L;
        Long menuId = -4L;
        ListCtrl listCtrl = listCtrlService.findListCtrl(userId, menuId);
        String hiddenColumn = StringUtils.isNotBlank(listCtrl.getHiddenColumn()) ? listCtrl.getHiddenColumn() : "";
        System.err.println("hiddenColumn:" + hiddenColumn);
    }

    @Test
    public void receive(){
        OrderStatusChangeFacade facade = new OrderStatusChangeFacade();
        facade.setUuid(GeneratorId.guuid());
        facade.setOrderId("20180917000001");
        facade.setOrderStatus(OrderStatusEnum.SysFailed.getValue());
        String msg = JSON.toJSONString(facade);
        orderStatusChangeReceiver.receive(msg);
        System.err.println("done");
    }
}
