import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.common.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void test() {
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        redisService.put(u1, u1);
        redisService.put(u2, u2);
        redisService.put(u3, u3);
        System.err.println(redisService.get(u1,String.class));
        System.err.println(redisService.get(u2,String.class));
        System.err.println(redisService.get(u3,String.class));
    }

    @Test
    public void test2(){
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        System.err.println(redisService.get(u1,String.class));
        System.err.println(redisService.get(u2,String.class));
        System.err.println(redisService.get(u3,String.class));
    }

    @Test
    public void test3(){
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        redisService.deleteWithPattern(prefix+"*");
    }

    @Test
    public void test4(){
        String prefix = "login_user_";
        Set<String>  keys = redisService.getKeysWithPattern(prefix+"*");
        System.err.println(JSON.toJSONString(keys));
        System.err.println("done");

        Set<String> dkeys=new HashSet<>();
        dkeys.add("login_user_3");
        dkeys.add("login_user_2");
        redisService.deleteWithKeys(dkeys);
    }

}
