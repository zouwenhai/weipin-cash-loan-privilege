package nirvana.cash.loan.privilege.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Administrator on 2018/11/1.
 */
public class URLQueryStringUtil {

    /**
     * 格式: a=1&b=2&c=3
     *
     * @param queryString
     * @return
     */
    public static String getValue(String queryString, String paramName) {
        if(StringUtils.isBlank(queryString)){
            return null;
        }
        String[] params = queryString.split("&");
        for (int i = 0; i < params.length; i++) {
            if(!params[i].contains("=")){
                continue;
            }
            String[] pair = params[i].split("=");
            if (paramName.equals(pair[0]) && pair.length == 2) {
                return pair[1];
            }
        }
        return null;
    }
}
