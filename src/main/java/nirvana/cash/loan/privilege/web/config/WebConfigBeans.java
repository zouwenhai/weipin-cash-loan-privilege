package nirvana.cash.loan.privilege.web.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.PostConstruct;

/**
 * 注册转换器
 * Created by Administrator on 2018/7/11.
 */
@Configuration
public class WebConfigBeans {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @PostConstruct
    public void initEditableAvlidation() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) handlerAdapter.getWebBindingInitializer();
        if (initializer.getConversionService() != null) {
            GenericConversionService genericConversionService = (GenericConversionService) initializer.getConversionService();
            //空字符串转NULL
            genericConversionService.addConverter(new Converter<String, String>() {
                @Override
                public String convert(String value) {
                    if (value != null && value.trim().length() == 0) return null;
                    return value;
                }
            });
        }

    }
}
