package nirvana.cash.loan.privilege.common.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

/**
 * Created by Administrator on 2018/11/8.
 */
@Slf4j
@Component
public class FreemarkerUtil {

    @Autowired
    Configuration configuration;

    public String resolve(String template, Object data) {
        try {
            Template t = null;
            t = configuration.getTemplate(template);
            return FreeMarkerTemplateUtils.processTemplateIntoString(t, data);
        } catch (IOException ex) {
            log.error("解析模板引擎失败|IOException exception:{}", ex);
        } catch (TemplateException ex) {
            log.error("解析模板引擎失败|TemplateException exception:{}", ex);
        }
        return null;
    }
}
