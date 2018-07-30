package nirvana.cash.loan.privilege.common.util;

import java.util.UUID;

/**
 * Created by Administrator on 2018/7/25.
 */
public class GeneratorId {

    public static String guuid(){
        return  UUID.randomUUID().toString().replace("-","");
    }

}
