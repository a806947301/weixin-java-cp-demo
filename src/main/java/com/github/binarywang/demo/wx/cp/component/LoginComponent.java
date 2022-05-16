package com.github.binarywang.demo.wx.cp.component;

import com.github.binarywang.demo.wx.cp.component.WxPermanentCode.Corp;
import com.github.binarywang.demo.wx.cp.config.WxCpProperties;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import java.util.Objects;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * @author wut
 * @since 2022-05-06
 */
@Component
public class LoginComponent {
    protected final static Logger logger = LoggerFactory.getLogger(LoginComponent.class);
    @Resource
    WxCpProperties wxCpProperties;

    @Resource
    private WxParam wxParam;

    /** 当前登录的用户 */
    private JSONObject loginUserInfo;

    /** 当前登录用户的openId */
    private String openId;

    public String getOpenId(Corp corp) {
        if (Objects.isNull(loginUserInfo)) {
            logger.error("还没有登录！！！");
            return null;
        }
        if (Objects.isNull(openId)) {
            try {
                openId = WxHttpUtil.getOpenId(WxHttpUtil.getAccessToken(wxParam.getSuiteAccessToken(), corp),
                    loginUserInfo.getString("userid"));
                return openId;
            } catch (JSONException e) {
                logger.error("getOpenId发生了异常！");
                e.printStackTrace();
                return null;
            }
        }
        return openId;
    }

    public String buildLoginUrl() {
        String url = "https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=" + wxCpProperties.getCorpId()
            +"&redirect_uri="
            + "http%3A%2F%2Fq3s1222063.wicp.vip%3A29082%2Flogin%2FdoLogin"
            +"&state=web_login@gyoss9&usertype=member";
        return url;
    }

    public String buildLoginUrlH5(Integer agentId) {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + wxCpProperties.getCorpId()
            +"&redirect_uri=" + "http%3A%2F%2Fq3s1222063.wicp.vip%3A29082%2Flogin%2FdoLogin"
            +"&response_type=code"
            +"&scope=snsapi_privateinfo"
            +"&agentid=" + agentId
            +"&state=123#wechat_redirect";
        return url;
    }


    public String getLoginUserInfo(String authCode) {
        return WxHttpUtil.getLoginUserInfo(wxParam.getProviderToken(), authCode);
    }

    public String getLoginUserInfoH5(String authCode) {
        return WxHttpUtil.getLoginUserInfoH5(wxParam.getSuiteAccessToken(), authCode);
    }

    public JSONObject getLoginUserInfo() {
        return loginUserInfo;
    }

    public LoginComponent setLoginUserInfo(JSONObject loginUserInfo) {
        this.loginUserInfo = loginUserInfo;
        return this;
    }
}
