import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.system.dao.UserMapper;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(124);
        roleIds.add(127);
        List<String> list = userMapper.findCollectionRoleNamesByRoleIds(roleIds);
        System.err.println(JSON.toJSONString(list));
    }

}
