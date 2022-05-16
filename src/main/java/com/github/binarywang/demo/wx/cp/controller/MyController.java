package com.github.binarywang.demo.wx.cp.controller;

import com.github.binarywang.demo.wx.cp.component.Department;
import com.github.binarywang.demo.wx.cp.component.WxParam;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode.Corp;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import com.google.gson.JsonObject;
import java.io.File;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/my")
public class MyController {
    protected final static Logger logger = LoggerFactory.getLogger(MyController.class);

    @Resource
    WxParam wxParam;

    @Resource
    private WxPermanentCode wxPermanentCode;
    @Resource
    private Department department;

    /**
     * 获取SuiteToken
     * @return
     */
    @GetMapping("/suiteToken")
    public String getSuiteToken() {
        return wxParam.getSuiteAccessToken();
    }

    /**
     * 准备调用企业授权接口
     * @return
     */
    @GetMapping("/callAuth")
    public String callAuth(String companyId) {
        return wxPermanentCode.buildAuthUrl(companyId);
    }

    /**
     * 企业微信回调
     * @param authCode
     * @param expiresIn
     * @param state
     * @return
     */
    @RequestMapping("/authCallBack")
    public String authCallBack(@RequestParam("auth_code") String authCode,
        @RequestParam("expires_in")String expiresIn, @RequestParam("state") String state) {
        logger.info("获取到了回调，authCode:{}， expiresIn:{}，state:{}", authCode, expiresIn, state);
        logger.info("准备获取永久授权码");
        wxPermanentCode.buildPermanentCode(state, authCode);
        return "success";
    }

    @GetMapping("/getDepartmentAndP")
    public String getDepartmentAndP(String companyId) {
        Corp corp = wxPermanentCode.getCorp(companyId);
        return department.getDepartment1(corp);
    }


    @GetMapping("/tranTong")
    public String tranTong(String companyId) {
        File file = new File("C:\\Users\\monda\\Desktop\\tran.txt");
        String mediaId = WxHttpUtil.uploadTongTranFile(wxParam.getProviderToken(), file);
        if (null == mediaId) {
            return "error";
        }

        Corp corp = wxPermanentCode.getCorp(companyId);
        String s = WxHttpUtil.tranTong(wxParam.getProviderToken(), mediaId, corp.getCorpId());
        return s;
    }

    @GetMapping("/getJob")
    public void getJob(String jobId, HttpServletResponse response) throws Exception{
        String taskResult = WxHttpUtil.getTaskResult(wxParam.getProviderToken(), jobId);
        JSONObject jsonObject = new JSONObject(taskResult);
        String url = jsonObject.getJSONObject("result").getJSONObject("contact_id_translate").getString("url");
        String content = "<br><a href=\"" + url + "\">hahahah</a>";
        response.getOutputStream().write(content.getBytes());
    }
}
