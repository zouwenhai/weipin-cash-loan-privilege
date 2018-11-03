//package nirvana.cash.loan.privilege.web.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import lombok.extern.slf4j.Slf4j;
//import nirvana.cash.loan.privilege.common.util.URLQueryStringUtil;
//import ListCtrl;
//import User;
//import ListCtrlService;
//import nirvana.cash.loan.privilege.web.RequestCheck;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StreamUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
///**
// * 列表控制显示过滤器
// */
//@Slf4j
//@Component
//public class ListCtrlZuulFilter extends ZuulFilter {
//
//    @Autowired
//    private RequestCheck requestCheck;
//    @Autowired
//    private ListCtrlService listCtrlService;
//
//    @Override
//    public String filterType() {
//        return FilterConstants.POST_TYPE;
//    }
//
//    @Override
//    public int filterOrder() {
//        return 1000;
//    }
//
//    @Override
//    public boolean shouldFilter() {
//        return true;
//    }
//
//    private final static String paramMenuId = "menuId";
//
//    @Override
//    public Object run() {
//        Long userId = null;
//        Long menuId = null;
//        try {
//            RequestContext ctx = RequestContext.getCurrentContext();
//            HttpServletRequest request = ctx.getRequest();
//            //查询登录用户信息，未查询到,不处理
//            User user = requestCheck.getLoginUser(request);
//            if (user == null) {
//                return null;
//            }
//            //查询字符串为空 或者 菜单ID,不处理
//            String queryParam = request.getQueryString();
//            log.info("queryParam:{}", queryParam);
//            if (StringUtils.isBlank(queryParam) || !queryParam.contains(paramMenuId)) {
//                return null;
//            }
//            //获取菜单ID值,若未获取到,不处理
//            String menuIdStr = URLQueryStringUtil.getValue(queryParam, paramMenuId);
//            if (StringUtils.isBlank(menuIdStr)) {
//                return null;
//            }
//            //query user list hiddenColumn
//            userId = user.getUserId();
//            menuId = Long.valueOf(menuIdStr);
//            log.info("query list hiddenColumn:userId={},menuId={}", userId, menuId);
//            ListCtrl listCtrl = listCtrlService.findListCtrl(userId, menuId);
//            if (listCtrl == null || StringUtils.isBlank(listCtrl.getHiddenColumn())) {
//                return null;
//            }
//            String hiddenColumn = listCtrl.getHiddenColumn();
//            InputStream stream = ctx.getResponseDataStream();
//            String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
//            if (StringUtils.isBlank(body)) {
//                body = "{}";
//            }
//            JSONObject resjson = JSONObject.parseObject(body);
//            resjson.put("hiddenColumn", hiddenColumn);
//            ctx.setResponseBody(resjson.toJSONString());
//        } catch (IOException ex) {
//            log.info("列表隐藏列查询失败:userId={},menuId={},exception={}", userId, menuId, ex);
//        }
//        return null;
//    }
//
//
//}