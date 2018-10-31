package com.hh.api.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.jfinal.kit.HashKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.platform.annotation.Service;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseService;
import com.platform.tools.ToolMail;

@Service(name = APIAuthService.serviceName)
public class APIAuthService extends BaseService {

    private final static Logger log = LoggerFactory.getLogger(APIAuthService.class);

    public static final String serviceName = "apiAuthService";

    public void sendTextMail(List<String> users, String subject, String content) {
        sendTextMail(users, subject, content, ToolMail.sendType_text, null);
    }

    private void sendTextMail(List<String> list, String subject, String content, String sendType, String[] attachFileNames) {
        String host = PropKit.get(ConstantInit.config_mail_host);
        String port = PropKit.get(ConstantInit.config_mail_port);
        String userName = PropKit.get(ConstantInit.config_mail_userName);
        String pwd = PropKit.get(ConstantInit.config_mail_password);
        String from = PropKit.get(ConstantInit.config_mail_from);
        ToolMail.sendTextMail(host, port, true, userName, pwd, from, list, subject, content, attachFileNames);
    }



    /**
     * 发送短信
     *
     * @param phoneNumber 多个号码之间用英文半角分号隔开
     * @param msg         短信内容
     */
    public boolean sendSMS(String phoneNumber, String msg) {
        if (StringUtils.isAnyBlank(phoneNumber, msg)) {
            return false;
        }
        String url = PropKit.get(ConstantInit.CONFIG_SMS_URL);
        String user = PropKit.get(ConstantInit.CONFIG_SMS_USER);
        String pwd = PropKit.get(ConstantInit.CONFIG_SMS_PWD);
        String sign = PropKit.get(ConstantInit.CONFIG_SMS_SIGN);
        Map<String, String> params = new HashMap<>();
        params.put("Action", "SendSms");
        params.put("UserName", user);
        params.put("Password", HashKit.md5(pwd));
        params.put("Mobile", phoneNumber);
        try {
            params.put("Message", URLEncoder.encode(sign + msg, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        String res = HttpKit.post(url, params);
        log.info("短信发送结果:{}", res);
//        Document doc = Jsoup.parse(res);
        return true;
    }
}
