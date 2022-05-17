package com.github.binarywang.demo.wx.cp.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.demo.wx.cp.config.WxCpProperties;
import com.github.binarywang.demo.wx.cp.config.WxCpProperties.AppConfig;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * @author wut
 * @since 2022-05-05
 */
@Component
public class WxParam {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private WxCpProperties wxCpProperties;

    /** 服务商ticket  todo */
    private String suiteTicket = "E9LjsIu8Un-axque3D4vc8jgFYNbo9sdRUd3JngbO1ISw1EiOW0ez7jW-XhP7EEo";

    /** 服务商token */
    private String suiteAccessToken;
    /** 服务商token获取的时间 */
    private Date suiteAccessTokenTime;

    /** 服务商Token */
    private String providerToken;

    public String getSuiteTicket() {
        return suiteTicket;
    }

    public WxParam setSuiteTicket(String suiteTicket) {
        this.suiteTicket = suiteTicket;
        return this;
    }

    public String getProviderToken() {
        if (null == providerToken || providerToken.length() == 0) {
            providerToken = WxHttpUtil.getProviderSecret(wxCpProperties.getCorpId(), wxCpProperties.getProviderSecret());
        }

        return providerToken;
    }

    public WxParam setSuiteTicketAndGetToken(String suiteTicket) {
        setSuiteTicket(suiteTicket);

        // 如果需要重新获取token
        if (Objects.isNull(suiteAccessTokenTime) ||
            Instant.now().isAfter(suiteAccessTokenTime.toInstant().plus(2, ChronoUnit.HOURS))) {
            suiteAccessTokenTime = new Date();
            AppConfig appConfig = wxCpProperties.getAppConfigs().get(0);
            suiteAccessToken = WxHttpUtil.getSuiteToken(appConfig.getSuiteID(), appConfig.getSecret(), suiteTicket);
            logger.info("设置suiteAcessToken为：{}", suiteAccessToken);
        }
        return this;
    }

    public String getSuiteAccessToken() {
        // 如果需要重新获取token
        if (Objects.isNull(suiteAccessTokenTime) ||
            Instant.now().isAfter(suiteAccessTokenTime.toInstant().plus(2, ChronoUnit.HOURS))) {

            AppConfig appConfig = wxCpProperties.getAppConfigs().get(0);
            suiteAccessToken = WxHttpUtil.getSuiteToken(appConfig.getSuiteID(), appConfig.getSecret(), suiteTicket);
            logger.info("设置suiteAcessToken为：{}", suiteAccessToken);
            if (Objects.nonNull(suiteAccessToken)) {
                suiteAccessTokenTime = new Date();
            }
        }

        return suiteAccessToken;
    }
}
