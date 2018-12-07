package nirvana.cash.loan.privilege.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author wangqiang
 * @Date 2018/8/14 15:28
 **/
@Component
@Slf4j
public class EmaiUtil {

    @Autowired
    JavaMailSender jms;

    public ResResult sendEmail(String fromAddress, String toAddress, String title, String content) {

        try {
            //建立邮件消息
            SimpleMailMessage mainMessage = new SimpleMailMessage();
            //发送者
            mainMessage.setFrom(fromAddress);
            //接收者
            mainMessage.setTo(toAddress);
            //发送的标题
            mainMessage.setSubject(title);
            //发送的内容
            mainMessage.setText(content);
            jms.send(mainMessage);
            log.info("简单邮件发送成功,from={},to={}",fromAddress,toAddress);
        } catch (Exception e) {
            log.error("发送简单邮件失败", e);
            return ResResult.success("发送失败");
        }
        return ResResult.error("发送成功");
    }

    public ResResult sendEmail(String fromAddress, List<String> toAddresList, String title, String content) {
        try {
            //建立邮件消息
            SimpleMailMessage mainMessage = new SimpleMailMessage();
            //发送者
            mainMessage.setFrom(fromAddress);
            //设置多个收件人地址
            String[] toAddresArray = new String[toAddresList.size()];
            for (int i = 0; i < toAddresList.size(); i++) {
                toAddresArray[i] = toAddresList.get(i);
            }
            mainMessage.setTo(toAddresArray);
            //发送的标题
            mainMessage.setSubject(title);
            //发送的内容
            mainMessage.setText(content);
            jms.send(mainMessage);
            log.info("简单邮件发送成功,from={},to={}",fromAddress, JSON.toJSONString(toAddresList));
        } catch (Exception e) {
            log.error("发送简单邮件失败", e);
            return ResResult.success("发送失败");
        }
        return ResResult.error("发送成功");
    }

    public ResResult sendEmailHtml(String fromAddress, String toAddress, String title, String content) {
        try {
            //建立邮件消息
            MimeMessage message = jms.createMimeMessage();
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //发送者
            helper.setFrom(fromAddress);
            //接收者
            helper.setTo(toAddress);
            //发送的标题
            helper.setSubject(title);
            //发送的内容
            helper.setText(content, true);
            jms.send(message);
            log.info("html邮件发送成功,from={},to={}",fromAddress, toAddress);
        } catch (Exception e) {
            log.error(String.format("发送Html邮件失败,接收者：%s,主题：%s,邮件内容：%s", toAddress, title, content), e);
            return ResResult.error("发送失败");
        }
        return ResResult.success("发送成功");
    }

    public ResResult sendEmailHtml(String fromAddress, List<String> toAddresList, String title, String content) {
        try {
            //建立邮件消息
            MimeMessage message = jms.createMimeMessage();
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //发送者
            helper.setFrom(fromAddress);
            //设置多个收件人地址
            InternetAddress[] addressArray = new InternetAddress[toAddresList.size()];
            for (int i = 0; i < toAddresList.size(); i++) {
                addressArray[i] = new InternetAddress(toAddresList.get(i));
            }
            helper.setTo(addressArray);
            //发送的标题
            helper.setSubject(title);
            //发送的内容
            helper.setText(content, true);
            jms.send(message);
            log.info("html邮件发送成功,from={},to={}",fromAddress, JSON.toJSONString(toAddresList));
        } catch (Exception e) {
            log.error("发送Html邮件失败", e);
            return ResResult.error("发送失败");
        }
        return ResResult.success("发送成功");
    }

    public boolean verifyEmailFormat(String emailAddress){
        if(StringUtils.isNotBlank(emailAddress)) {
            return true;
        }
        return false;
    }

}
