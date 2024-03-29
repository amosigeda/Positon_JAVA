package com.platform.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.hh.api.vo.APIConstant;
import com.hh.api.vo.RespData;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.platform.config.JFinalConfig;
import com.platform.constant.*;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.grouprole.GroupRoleService;
import com.platform.mvc.operator.Operator;
import com.platform.mvc.stationoperator.StationOperatorService;
import com.platform.mvc.syslog.Syslog;
import com.platform.mvc.user.User;
import com.platform.mvc.usergroup.UserGroup;
import com.platform.plugin.ServicePlugin;
import com.platform.tools.ToolDateTime;
import com.platform.tools.ToolRandoms;
import com.platform.tools.ToolWeb;
import com.platform.tools.security.ToolIDEA;

/**
 * 权限认证拦截器
 *
 * @author 董华健
 * 描述：
 * 1.处理权限验证
 * 2.处理全局异常
 * 3.处理权限相关的工具类方法
 */
public class AuthInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public void intercept(Invocation invoc) {
        BaseController contro = (BaseController) invoc.getController();
        HttpServletRequest request = contro.getRequest();
        HttpServletResponse response = contro.getResponse();

        log.debug("获取reqSysLog!");
        Syslog reqSysLog = contro.getAttr(ConstantWebContext.reqSysLogKey);
        contro.setReqSysLog(reqSysLog);

        log.debug("获取用户请求的URI，两种形式，参数传递和直接request获取");
        String uri = invoc.getActionKey(); // 默认就是ActionKey
        if (invoc.getMethodName().equals(ConstantWebContext.request_toUrl)) {
            uri = ToolWeb.getParam(request, ConstantWebContext.request_toUrl); // 否则就是toUrl的值
        }

        log.debug("获取当前用户!");
        boolean userAgentVali = true; // 是否验证userAgent，默认是
        if (uri.equals("/platform/ueditor") || uri.equals("/platform/upload") || uri.startsWith("/api")) { // 针对ueditor特殊处理，flash上传userAgent和浏览器并不一致
            userAgentVali = false;
        }
        User user = getCurrentUser(request, response, userAgentVali);// 当前登录用户
        if (null != user) {
            reqSysLog.set(Syslog.column_userids, user.getPKValue());
            contro.setAttr(ConstantWebContext.request_cUser, user);
            contro.setAttr(ConstantWebContext.request_cUserIds, user.getPKValue());

            MDC.put("userIP", reqSysLog.getIps());
            MDC.put("userId", user.getPKValue());
            MDC.put("userName", user.getUsername());
        } else {
            MDC.put("userIP", reqSysLog.getIps());
            MDC.put("userId", "*unknown userId*");
            MDC.put("userName", "*unknown userName*");
        }

        if (StringUtils.startsWithIgnoreCase(uri, "/api")) {
            if (!JFinalConfig.noValidPaths.contains(uri) && (user == null || StringUtils.isBlank(user.getIds()))) {
                log.error("用户信息错误，需要登录！");
                contro.renderJson(new RespData(ConstantLogin.login_info_99, "Need login."));
            } else {
                try {
                    invoc.invoke();
                } catch (Exception e) {
                    log.error("返回失败提示页面!Exception = {}", e.getMessage(), e);
                    contro.renderJson(new RespData(-1, e.getMessage()));
                }
            }
            return;
        }

        log.debug("获取URI对象!");
        Operator operator = Operator.cacheGet(uri);

        log.debug("判断URI是否存在!");
        if (null == operator) {
            log.debug("URI不存在!uri = {}", uri);

            log.debug("访问失败时保存日志!");
            reqSysLog.set(Syslog.column_status, "0"); // 失败
            reqSysLog.set(Syslog.column_description, "URL不存在");
            reqSysLog.set(Syslog.column_cause, "1"); // URL不存在

            toView(contro, ConstantAuth.auth_no_url, "权限认证过滤器检测：URI不存在");
            return;
        }

        log.debug("URI存在!");
        reqSysLog.set(Syslog.column_operatorids, operator.getPKValue());
        reqSysLog.setSyslog(operator.getSyslog()); // 指定日志是否入库

        log.debug("method校验");
        String method = request.getMethod().toLowerCase();
        if (("1".equals(operator.getMethod()) && !method.equals("get")) || "2".equals(operator.getMethod()) && !method.equals("post")) {
            String msg = "method校验失败，operator.method=" + operator.getMethod() + "，request.method=" + method;
            if (log.isInfoEnabled()) log.info(msg);
            toView(contro, ConstantAuth.auth_method, "权限认证过滤器检测：请求方法错误，" + msg);
            return;
        }

        log.debug("enctype校验");
        String enctype = operator.getEnctype();
        if (method.equals("post") && !"0".equals(enctype)) {
            String contentType = request.getContentType().toLowerCase();
            if (("1".equals(enctype) && !contentType.contains("application/x-www-form-urlencoded"))
                    || ("2".equals(enctype) && !contentType.contains("multipart/form-data"))
                    || ("3".equals(enctype) && !contentType.contains("text/plain"))) {
                String msg = "enctype校验失败，operator.enctype=" + enctype + "，request.contentType=" + contentType;
                if (log.isInfoEnabled()) log.info(msg);
                toView(contro, ConstantAuth.auth_enctype, "权限认证过滤器检测：请求编码错误，" + msg);
                return;
            }
        }

        log.debug("csrf校验");
        if (user != null) { // 理论上csrf安全涉及到的是后台数据更新和删除操作URL安全问题，所以不可能存在用户未登录情况
            if ("1".equals(operator.getCsrf())) {
                String csrfToken = request.getHeader("csrfToken");
                if (StrKit.isBlank(csrfToken)) {
                    csrfToken = contro.getPara("csrfToken");
                }
                if (StrKit.isBlank(csrfToken)) {
                    String msg = "csrf校验失败，当前请求没有提交csrfToken参数";
                    if (log.isInfoEnabled()) log.info(msg);
                    toView(contro, ConstantAuth.auth_csrf_empty, "权限认证过滤器检测：" + msg);
                    return;
                }

                String csrfTokenDecrypt = ToolIDEA.decrypt(user.getIdea(), csrfToken); // 使用用户私有密钥，不易伪造，可以定期更新密钥
                long csrfTokenTime = Long.parseLong(csrfTokenDecrypt.split(".#.")[1]);
                Date start = ToolDateTime.getDate();
                start.setTime(csrfTokenTime); // csrfToken生成时间
                int minute = ToolDateTime.getDateMinuteSpace(start, ToolDateTime.getDate()); // 已经生成多少分钟
                if (minute > 30) {
                    String msg = "超过30分钟，视为无效Token，需要刷新页面重新提交";
                    if (log.isInfoEnabled()) log.info(msg);
                    toView(contro, ConstantAuth.auth_csrf, "权限认证过滤器检测：" + msg);
                    return;
                }
            }
            boolean referer = ToolWeb.authReferer(request);
            if (referer) {
                String csrfToken = ToolIDEA.encrypt(user.getIdea(), ToolRandoms.getUuid(true) + ".#." + ToolDateTime.getDateByTime()); // 生成随机csrfToken，传递给页面使用
                contro.setAttr(ConstantWebContext.request_csrfToken, csrfToken);
            }
        }

//        log.debug("referer校验");
//        if ("1".equals(operator.getReferer())) {
//            boolean referer = ToolWeb.authReferer(request);
//            if (!referer) {
//                String msg = "referer校验失败";
//                if (log.isInfoEnabled()) log.info(msg);
//                toView(contro, ConstantAuth.auth_referer, "权限认证过滤器检测：" + msg);
//                return;
//            }
//        }

        log.debug("是否需要表单重复提交验证!");
        if ("1".equals(operator.getStr(Operator.column_formtoken))) {
            String tokenRequest = ToolWeb.getParam(request, ConstantWebContext.request_formToken);
            synchronized (log) {
                List keys = CacheKit.getKeys(ConstantCache.CACHE_EHCACHE_FORM_TOKEN);
                if (StringUtils.isNotBlank(tokenRequest) && (keys == null || !keys.contains(tokenRequest))) {
                    CacheKit.put(ConstantCache.CACHE_EHCACHE_FORM_TOKEN, tokenRequest, 1);
                } else if (StringUtils.isNotBlank(tokenRequest) && keys != null && keys.contains(tokenRequest)) {
                    log.debug("表单重复提交!");
                    toView(contro, ConstantAuth.auth_info, "请不要重复提交页面！");
                    return;
                }
            }
        }

        log.debug("是否需要权限验证!");
        if ("1".equals(operator.get(Operator.column_privilegess))) {
            log.debug("需要权限验证!");
            if (user == null) {
                log.debug("权限认证过滤器检测:未登录!");

                reqSysLog.set(Syslog.column_status, "0");// 失败
                reqSysLog.set(Syslog.column_description, "未登录");
                reqSysLog.set(Syslog.column_cause, "2");// 2 未登录

                toView(contro, ConstantAuth.auth_no_login, "权限认证过滤器检测：未登录");
                return;
            }

            if (!hasPrivilegeUrl(operator.getPKValue(), user.getPKValue())) {// 权限验证
                log.debug("权限验证失败，没有权限!");

                reqSysLog.set(Syslog.column_status, "0");// 失败
                reqSysLog.set(Syslog.column_description, "没有权限!");
                reqSysLog.set(Syslog.column_cause, "0");// 没有权限

                log.debug("返回失败提示页面!");
                toView(contro, ConstantAuth.auth_no_permissions, "权限验证失败，您没有操作权限");
                return;
            }
        }

        log.debug("不需要权限验证，或权限认证成功!!!继续处理请求...");

        log.debug("权限认证成功更新日志对象属性!");
        reqSysLog.set(Syslog.column_status, "1");// 成功
        Date actionStartDate = ToolDateTime.getDate();// action开始时间
        reqSysLog.set(Syslog.column_actionstartdate, ToolDateTime.getSqlTimestamp(actionStartDate));
        reqSysLog.set(Syslog.column_actionstarttime, actionStartDate.getTime());

        try {
                    invoc.invoke();
        } catch (Exception e) {
            String expMessage = e.getMessage();
            // 开发模式下的异常信息
            if (Boolean.parseBoolean(PropKit.get(ConstantInit.config_devMode))) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                e.printStackTrace(new PrintWriter(buf, true));
                expMessage = buf.toString();
                expMessage = StringUtils.replace(expMessage, "\n", "<br />");
            }

            log.error("业务逻辑代码遇到异常时保存日志!");
            reqSysLog.set(Syslog.column_status, "0");// 失败
            reqSysLog.set(Syslog.column_description, expMessage);
            reqSysLog.set(Syslog.column_cause, "3");// 业务代码异常

            log.error("返回失败提示页面!Exception = {}", e.getMessage(), e);
            toView(contro, ConstantAuth.auth_exception, "业务逻辑代码遇到异常Exception = " + expMessage);
        } finally {
            MDC.remove("userIP");
            MDC.remove("userId");
            MDC.remove("userName");
        }
    }

    /**
     * 提示信息展示页
     *
     * @param contro
     * @param type
     * @param msg
     */
    private void toView(BaseController contro, String type, String msg) {
        String isAjax = contro.getRequest().getHeader("X-Requested-With");

        if (ConstantAuth.auth_no_login.equals(type)) {// 未登录处理
            contro.redirect("/platform/login");
            return;
        }

        contro.setAttr("msg", msg);

        if (isAjax != null && isAjax.equalsIgnoreCase("XMLHttpRequest")) {
            contro.render("/platform/common/msgAjax.html"); // Ajax页面
        } else if(ConstantAuth.auth_info.equalsIgnoreCase(type)){
            contro.render("/hh/common/msg.html");
        } else {
            contro.render("/platform/common/msg.html"); // 完整html页面
        }
    }

    /**
     * 判断用户是否拥有某个功能的操作权限
     *
     * @param operatorIds
     * @param userIds
     * @return
     */
    public static boolean hasPrivilegeUrl(String operatorIds, String userIds) {
        Operator op = Operator.dao.findById(operatorIds);
        if (null == op || StringUtils.isBlank(userIds)) {
            return false;
        }
        if ("/".equalsIgnoreCase(op.getUrl()) || "/com/comm".equals(op.getUrl())) {
            return true;
        }
        if ("0".equals(op.getPrivilegess())) {
            return true;
        }

        /**
         * 1.直接查询数据库表验证操作权限
         *
         // 根据分组角色查询权限
         String roleSql = ToolSqlXml.getSqlMy("platform.roleOperator.hasUrlByOperatorAndUserIds");
         long roleCount = Db.use(ConstantInit.db_dataSource_main).queryNumber(roleSql, operatorIds, userIds).longValue();
         if (roleCount > 0) {
         return true;
         }

         // 根据岗位查询权限
         String stationSql = ToolSqlXml.getSqlMy("platform.stationOperator.hasUrlByOperatorAndUserIds");
         long stationCount = Db.use(ConstantInit.db_dataSource_main).queryNumber(stationSql, operatorIds, userIds).longValue();
         if (stationCount > 0) {
         return true;
         }
         **/

        /**
         * 2.取缓存验证操作权限
         **/

        // 根据分组角色查询权限
        User user = User.cacheGetByUserId(userIds);
        List<UserGroup> ugList = user.get("ugList");
        for (UserGroup ug : ugList) {
            String groupIds = ug.getGroupids();
            GroupRoleService groupRoleService = (GroupRoleService) ServicePlugin.getService(GroupRoleService.serviceName);
            Ret ret = groupRoleService.selectRole(groupIds);
            List<Record> checkedList = (List<Record>) ret.get("checkedList");
            if (checkedList != null) {
                for (Record rec : checkedList) {
                    if (ConstantAuth.NUMBER_SUPER_ADMIN.equals(rec.get("rnumbers"))) {
                        return true;
                    }
                }
            }
            List<Operator> oList = GroupRoleService.cacheGet(groupIds);
            for (Operator operator : oList) {
                if (operatorIds.equals(operator.getPKValue())) {
                    return true;
                }
            }
        }

        // 根据岗位查询权限
        String stationIds = user.getStationids();
        List<Operator> oList = StationOperatorService.cacheGet(stationIds);
        for (Operator operator : oList) {
            if (operatorIds.equals(operator.getPKValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @param response
     * @param userAgentVali 是否验证 User-Agent
     * @return
     */
    public static User getCurrentUser(HttpServletRequest request, HttpServletResponse response, boolean userAgentVali) {
		return getCurrentUser(request, response, null, userAgentVali);
	}

    /**
     * 获取当前登录用户
     *
     * @param request
     * @param response
     * @param authmark
     * @param userAgentVali 是否验证 User-Agent
     * @return
     */
	public static User getCurrentUser(HttpServletRequest request, HttpServletResponse response, String authmark, boolean userAgentVali) {
        // 加密串存储位置，默认先取header
		if(StrKit.isBlank(authmark)){
			authmark = request.getHeader(ConstantWebContext.cookie_authmark);
        }

        // 如果为空，再取cookie
        if (StrKit.isBlank(authmark)) {
            authmark = ToolWeb.getCookieValueByName(request, ConstantWebContext.cookie_authmark);
        }

        if (StringUtils.isBlank(authmark)) {
            authmark = response.getHeader(ConstantWebContext.cookie_authmark);
        }

        // 登录cookie标识异常
        if (StrKit.isBlank(authmark)) {
            ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.cookie_authmark, null, 0);
            return null;
        }

        // 1.解密认证数据，外层
        String outer = ToolIDEA.decrypt(authmark);
        if (StrKit.isBlank(outer)) {
            ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.cookie_authmark, null, 0);
            return null;
        }
        String[] outerArr = outer.split(".#.");    // arr[0]：USERID，arr[1]：inner
        String userIds = outerArr[0];
        User user = User.cacheGetByUserId(userIds);
        if (user == null) {
            ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.cookie_authmark, null, 0);
            return null;
        }
        String secretkey = user.getIdea();
        String inner = null;
        try {
            inner = ToolIDEA.decrypt(secretkey, outerArr[1]);
        } catch (Exception e) {
            //
        }
        if (inner == null) {
            ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.cookie_authmark, null, 0);
            return null;
        }
        // 2. 分解认证数据，内层
        String[] innerArr = inner.split(".#.");        // arr[0]：scheme，arr[1]：时间戳，arr[2]：USER_IP， arr[3]：USER_AGENT， arr[4]：autoLogin
        String scheme;              // scheme
        long loginDateTimes;        // 时间戳
//        String ips;                 // ip地址
        String userAgent;           // USER_AGENT
        boolean autoLogin;          // 是否自动登录

        if (innerArr.length == 5) {
            scheme = innerArr[0];
            loginDateTimes = Long.parseLong(innerArr[1]);
//            ips = innerArr[2];
            userAgent = innerArr[3];
            autoLogin = Boolean.valueOf(innerArr[4]);
        } else {
            //兼容旧版本
            scheme = null;
            loginDateTimes = Long.parseLong(innerArr[0]);
//            ips = innerArr[1];
            userAgent = innerArr[2];
            autoLogin = Boolean.valueOf(innerArr[3]);
        }

        // 3.用户当前数据
        String newScheme = request.getScheme();
//        String newIp = ToolWeb.getIpAddr(request);
        String newUserAgent = request.getHeader("User-Agent");

        Date start = ToolDateTime.getDate();
        start.setTime(loginDateTimes); // 用户自动登录开始时间
        int day = ToolDateTime.getDateDaySpace(start, ToolDateTime.getDate()); // 已经登录多少天
        int maxAge = PropKit.getInt(ConstantInit.config_maxAge_key);    // cookie自动登录有效天数

        // 4. 验证数据有效性
        if (newScheme.equals(scheme)
                //&& ips.equals(newIp)
                && (!userAgentVali || userAgent.equals(newUserAgent))
                && day <= maxAge) {
            // 如果非记住密码（非自动登录），单次登陆有效时间验证
            if (!autoLogin) {
                int minute = ToolDateTime.getDateMinuteSpace(start, new Date());
                int session = PropKit.getInt(ConstantInit.config_session_key);
                if (minute > session) {
                    return null;
                } else {
                    setCurrentUser(request, response, user, autoLogin); // 重写登录标识cookie
                }
            }

            // 返回用户数据
            return User.cacheGetByUserId(userIds);
        }
        return null;
    }


    /**
     * 获取API当前登录用户
     *
     * @return
     */
    public static User getAPICurrentUser(HttpServletRequest request, HttpServletResponse response) {
        return getCurrentUser(request, response, false);
    }

    /**
     * 设置当前登录用户到cookie
     *
     * @param request
     * @param response
     * @param user
     * @param autoLogin 描述：加密串分两层，外层使用系统公共密钥加密，内层使用用户私有密钥加密
     */
	public static String setCurrentUser(HttpServletRequest request, HttpServletResponse response, User user, boolean autoLogin) {
        // 1.设置cookie有效时间
        int maxAgeTemp = -1;
        if (autoLogin) {
            maxAgeTemp = PropKit.getInt(ConstantInit.config_maxAge_key);
        }

        // 2.设置用户名到cookie
        ToolWeb.addCookie(request, response, "", null, true, "userName", user.getUsername(), maxAgeTemp);

        // 3.生成登陆认证cookie
        String secretkey = user.getIdea();
        String userIds = user.getPKValue();

        String scheme = request.getScheme();
        String ips = ToolWeb.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
                        long date = ToolDateTime.getDateByTime();

		// 内层用户私有密钥加密
		String inner = new StringBuilder(scheme) // scheme.#.时间戳.#.USER_IP.#.USER_AGENT.#.autoLogin
			.append(".#.").append(date)
			.append(".#.").append(ips)
			.append(".#.").append(userAgent)
			.append(".#.").append(autoLogin)
			.toString();
		inner = ToolIDEA.encrypt(secretkey, inner);

        // 外层使用系统公共密钥加密
        String outer = new StringBuilder(userIds) // userIds.#.inner
                .append(".#.").append(inner)
                .toString();
        outer = ToolIDEA.encrypt(outer);

        // 4. 添加到Cookie和header
        ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.cookie_authmark, outer, maxAgeTemp);
        //虽然APIConstant.ACCESS_KEY==ConstantWebContext.cookie_authmark，但为预防后期更改造成bug，故均各设置一次
        response.setHeader(ConstantWebContext.cookie_authmark, outer);
        response.setHeader(APIConstant.ACCESS_KEY, outer);
        response.setHeader(APIConstant.ACCESS_UID, userIds);
		
		return outer;
                        }

    /**
     * 设置验证码
     *
     * @param response
     * @param authCode
     */
    public static void setAuthCode(HttpServletRequest request, HttpServletResponse response, String authCode) {
        // 1.生成验证码加密cookie
//        String authCodeCookie = ToolIDEA.encrypt(authCode);

        // 2.设置登陆验证码cookie
        int maxAgeTemp = 30 * 60; // 半小时
        String sessionId = request.getSession().getId();
        ToolWeb.addCookie(request, response, "", null, true, "JSESSIONID", sessionId, maxAgeTemp);
        CacheKit.put(ConstantCache.CACHE_NAME_PIN_CODE, APIConstant.PREFIX_PIN + sessionId, authCode);
        log.debug("\n========\nSave SessionId:{} authCode:{}\n========\n", sessionId, authCode);
                }

    /**
     * 获取验证码
     *
     * @param request
     * @param response
     * @return
     */
    public static String getAuthCode(HttpServletRequest request, HttpServletResponse response) {
        // 1.获取cookie加密数据
//        String authCode = ToolWeb.getCookieValueByName(request, ConstantWebContext.request_authCode);
        String sessionId = request.getSession().getId();
        String authCode = CacheKit.get(ConstantCache.CACHE_NAME_PIN_CODE, APIConstant.PREFIX_PIN + sessionId);
        log.debug("\n========\nGet SessionId:{} authCode:{}\n========\n", sessionId, authCode);
        return authCode;
        // 2.获取验证码后清除客户端验证码信息
        /*ToolWeb.addCookie(request, response, "", null, true, ConstantWebContext.request_authCode, null, 0);

        // 3.解密数据
        if (null != authCode && !authCode.equals("")) {
            authCode = ToolIDEA.decrypt(authCode);
            return authCode;
        }

        return null;*/
    }

}
