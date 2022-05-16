package com.github.binarywang.demo.wx.cp.component;

import com.github.binarywang.demo.wx.cp.config.WxCpProperties;
import com.github.binarywang.demo.wx.cp.controller.MyController;
import com.github.binarywang.demo.wx.cp.utils.HttpClient;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wut
 * @since 2022-05-05
 */
@Component
public class WxPermanentCode {
    protected final static Logger logger = LoggerFactory.getLogger(WxPermanentCode.class);
    @Resource
    private WxParam wxParam;
    @Resource
    private WxCpProperties wxCpProperties;

    private Map<String, Corp> map = new HashMap<>();
    {
        Corp corp = new Corp().setCorpId("ww373fefe0069618ef").setCode("7kNXiHBVyhVbwvRqiuJfbQUAFkqbrJiPOg5zqR7MHeo")
            .setAgentId(1000027)
            .setCharSecret("nQZKNJoSy1k2673qP6mUGL4CiXdO5obvj_jyrlfr8C0");
        //
        map.put("company1",corp);

        Corp corp2 = new Corp().setCorpId("wpaNQUCgAAiCm1mjVRD64EyfjzgIIQ3g").setCode("6Nb6A7GwGqu52btuohKqO4-_TDnwhE1acoV7eAp8SIM")
            .setCharSecret("IciBDFLhbEInfun-nygritKNp2lSELcEIuNgVgMTjWc");
        map.put("company2",corp2);

        Corp corp3 = new Corp().setCorpId("wpaNQUCgAABr0K8QFzBWJqzHswRtzc2Q").setCode("QQA9qJ5J7CZyS5ItlxNb_Gm33UNkT5ML69r3LKJTOxw")
            .setCharSecret("9G5iJaf9nKH1upxAAF3Kn8_DP_fO1LY-nyOfT7M04sQ");
        map.put("company3",corp3);
    }

    public static class Corp {
        public String corpId;
        public String code;
        public String charSecret;
        public Integer agentId;

        public Integer getAgentId() {
            return agentId;
        }

        public Corp setAgentId(Integer agentId) {
            this.agentId = agentId;
            return this;
        }

        public String getCharSecret() {
            return charSecret;
        }

        public Corp setCharSecret(String charSecret) {
            this.charSecret = charSecret;
            return this;
        }

        public String getCorpId() {
            return corpId;
        }

        public Corp setCorpId(String corpId) {
            this.corpId = corpId;
            return this;
        }

        public String getCode() {
            return code;
        }

        public Corp setCode(String code) {
            this.code = code;
            return this;
        }
    }


    /**
     * 获取某个企业的永久授权码
     * @param companyId
     * @return
     */
    public Corp getCorp(String companyId) {
        return map.get(companyId);
    }

    public void putCorp(String companyId, Corp code) {

        map.put(companyId, code);
    }

    /**
     * 构造某个企业企业微信授权的url
     * @param companyId
     * @return
     */
    public String buildAuthUrl(String companyId) {
        String url =
            "https://open.work.weixin.qq.com/3rdapp/install?suite_id=SUITE_ID&pre_auth_code=PRE_AUTH_CODE&redirect_uri=REDIRECT_URI&state=STATE";
        String preAuthCode = WxHttpUtil.getPreAuthCode(wxParam.getSuiteAccessToken());
        WxHttpUtil.setPermission(preAuthCode, wxParam.getSuiteAccessToken());
        logger.info("设置权限完成-----------------------");

        url = url.replaceAll("SUITE_ID", wxCpProperties.getAppConfigs().get(0).getSuiteID());
        url = url.replaceAll("PRE_AUTH_CODE", preAuthCode);
        url = url.replaceAll("REDIRECT_URI", "http://q3s1222063.wicp.vip:29082/my/authCallBack");
        url = url.replaceAll("STATE", companyId);

        return url;
    }

    /**
     * 构造永久授权码
     * @param authCode
     * @return
     */
    public boolean buildPermanentCode(String companyId, String authCode) {
        Corp corp = WxHttpUtil.getPermanentCorp(authCode, wxParam.getSuiteAccessToken());
        if (null == corp) {
            logger.error("获取永久授权码失败");
            return false;
        }
        putCorp(companyId, corp);
        return true;
    }
}
