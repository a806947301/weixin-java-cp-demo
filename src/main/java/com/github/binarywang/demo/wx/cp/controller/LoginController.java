package com.github.binarywang.demo.wx.cp.controller;

import com.github.binarywang.demo.wx.cp.component.Department;
import com.github.binarywang.demo.wx.cp.component.LoginComponent;
import com.github.binarywang.demo.wx.cp.component.WxParam;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode.Corp;
import com.github.binarywang.demo.wx.cp.config.WxCpProperties;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wut
 * @since 2022-05-05
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    protected final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    WxParam wxParam;

    @Resource
    private WxPermanentCode wxPermanentCode;
    @Resource
    private Department department;

    @Resource
    private LoginComponent loginComponent;

    @Resource
    private WxCpProperties wxCpProperties;

    /**
     * 获取登录的url
     * @return
     */
    @GetMapping("/loginUrl")
    public void bulidLoginUrl(HttpServletResponse response) {
        String content = "<a target=\"_blank\" href=\"" + loginComponent.buildLoginUrl() + "\">跳转域名</a>";
        try {
            response.getOutputStream().write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取登录的url
     * @return
     */
    @GetMapping("/loginUrlH5")
    public void bulidLoginUrlH5(HttpServletResponse response) {
        Corp corp = wxPermanentCode.getCorp("company1");
        String content = "<a target=\"_blank\" href=\"" + loginComponent.buildLoginUrlH5(corp.getAgentId()) + "\">跳转H5域名</a>";

//        String content = "<button type=\"button\" onclick=\"login()\">普通标签</button><script>function login() {window.location.href=\""+
//            loginComponent.buildLoginUrlH5() + "\"}</script>";
        try {
            response.getOutputStream().write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫码登录
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam(value = "auth_code", required = false) String authCode, @RequestParam(value = "code", required = false) String code) {
        logger.info("企业微信扫码登录，authCode: {} , code: {}", authCode, code);
        if (Objects.nonNull(authCode)) {
            return getLoginUserInfo(authCode);
        } else {
            return getLoginUserInfoH5(code);
        }
    }

    /**
     * 获取登录用户的信息
     * @param authCode
     * @return
     */
    @GetMapping("/loginUserInfo")
    public String getLoginUserInfo(String authCode) {
        String loginInfo = loginComponent.getLoginUserInfo(authCode);
        if (loginInfo == null) {
            return "error";
        }
        try {
            JSONObject jsonObject = new JSONObject(loginInfo);
            jsonObject = jsonObject.getJSONObject("user_info");
            String userid = jsonObject.getString("userid");

            String token = WxHttpUtil.getAccessToken(wxParam.getSuiteAccessToken(), wxPermanentCode.getCorp("company1"));
            String person = WxHttpUtil.getPerson(token, userid);
            loginComponent.setLoginUserInfo(new JSONObject(person));
            return person;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logger.error("拿不到登录信息");

        return loginInfo;
    }    /**
     * 获取登录用户的信息
     * @param authCode
     * @return
     */


    @GetMapping("/loginUserInfoH5")
    public String getLoginUserInfoH5(String authCode) {
        String loginInfo = loginComponent.getLoginUserInfoH5(authCode);
        if (loginInfo == null) {
            return "error";
        }
        try {
            JSONObject jsonObject = new JSONObject(loginInfo);
            String userTicket = jsonObject.getString("user_ticket");

//            String token = WxHttpUtil.getAccessToken(wxParam.getSuiteAccessToken(), wxPermanentCode.getCorp("company1"));
            String person = WxHttpUtil.getLoginUserDetailH5(wxParam.getSuiteAccessToken(), userTicket);
            loginComponent.setLoginUserInfo(new JSONObject(person));
            return person;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        logger.error("拿不到登录信息");

        return loginInfo;
    }

    @GetMapping("/adminList")
    public String adminList(String companyId) {
        Corp corp = wxPermanentCode.getCorp(companyId);

        return WxHttpUtil.getAdminList(wxParam.getSuiteAccessToken(), corp.getCorpId(), corp.getAgentId());
    }
}
