package nirvana.cash.loan.privilege.system.controller;

import nirvana.cash.loan.privilege.common.config.FebsProperies;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.util.vcode.Captcha;
import nirvana.cash.loan.privilege.common.util.vcode.GifCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018/8/1.
 */
@Controller
@RequestMapping("/privilige")
public class ImgCodeController extends BaseController {

    @Autowired
    private FebsProperies febsProperies;

    //生成图形验证码
    @GetMapping(value = "/notauth/gifCode")
    public void getGifCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");

            Captcha captcha = new GifCaptcha(
                    febsProperies.getValidateCode().getWidth(),
                    febsProperies.getValidateCode().getHeight(),
                    febsProperies.getValidateCode().getLength());
            captcha.out(response.getOutputStream());
            //图形验证码,缓存5min
            redisService.putWithExpireTime("_code",captcha.text().toLowerCase(),1000 * 60 * 5L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
