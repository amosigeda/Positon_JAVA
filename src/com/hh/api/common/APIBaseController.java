package com.hh.api.common;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hh.api.tools.CommUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.hh.api.tools.FileUtils;
import com.platform.annotation.Controller;
import com.platform.interceptor.AuthInterceptor;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.user.User;

/**
 * Created by Administrator on 2016/11/29.
 */
@Controller("/api")
public class APIBaseController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(APIBaseController.class.getName());
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, String urlPara) {
        super.init(request, response, urlPara);
        this.request = request;
        this.response = response;
    }

    /**
     * 获取网络JSON参数
     */
    public JSONObject getHttpJson() {
        byte[] req = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ServletInputStream servletInputStream = getRequest().getInputStream();
            byte[] b = new byte[1024];
            int i;
            while ((i = servletInputStream.read(b, 0, 1024)) > 0) {
                out.write(b, 0, i);
            }
            req = out.toByteArray();

            return JSONObject.parseObject(req, JSONObject.class);
        } catch (Exception e) {
            logger.error("格式化json出错，请确认传入内容符合json格式！ 传入内容：{}", CommUtil.toStr(req), e);
            return new JSONObject();
        } finally {
            FileUtils.closeStream(out);
        }
    }

    /**
     * 获取网络数据
     */
    public byte[] getHttpBytes() {
        ByteArrayOutputStream out = null;
        byte[] bts = null;
        try {
            ServletInputStream servletInputStream = getRequest().getInputStream();
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i;
            while ((i = servletInputStream.read(b, 0, 1024)) > 0) {
                out.write(b, 0, i);
            }
            bts = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeStream(out);
        }
        return bts;
    }


    /**
     * 获取网络JSON参数
     *
     * @throws java.io.IOException
     */
//    public LoginInfo getLoginInfo() throws IOException {
//        LoginInfo loginInfo;
//        return loginInfo;
//    }

    /**
     * 获取跟路径
     *
     * @return
     */
    public String getBaseUrl() {
        String reqUrl = CommUtil.getBaseUrl(getRequest().getRequestURL().toString());
        String ctxPath = getRequest().getContextPath();
        return reqUrl + ctxPath;
    }

    public User getCurrentUser() {
        return AuthInterceptor.getAPICurrentUser(request, response);
    }

    public Locale getCurrentLocale() {
        String locale = request.getHeader("locale");
        if (StringUtils.isBlank(locale)) {
            return Locale.ENGLISH;
        }
        String[] tokens = StringUtils.split(locale, "_");
        Locale loc;
        if (tokens.length > 1) {
            loc = new Locale(tokens[0], tokens[1]);
        } else if (tokens.length > 0) {
            loc = new Locale(tokens[0]);
        } else {
            loc = Locale.ENGLISH;
        }
        return loc;
    }
}
