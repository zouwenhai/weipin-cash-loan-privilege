package nirvana.cash.loan.privilege.controller.task;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.service.AuthCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Slf4j
@Component
public class ScheduledController {

    @Autowired
    private AuthCacheService authCacheService;

    /**
     * 每日3点，删除前一日用户登录缓存信息
     * @throws ParseException
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteAuthCache() throws ParseException {
        authCacheService.deleteAuthCache();
    }
}
