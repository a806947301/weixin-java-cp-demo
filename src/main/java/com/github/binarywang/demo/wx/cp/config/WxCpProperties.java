package com.github.binarywang.demo.wx.cp.config;

import com.github.binarywang.demo.wx.cp.utils.JsonUtils;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "wechat.cp")
public class WxCpProperties {

    /**
     * 设置企业微信的corpId
     */
    private String corpId;

    /** 服务商Secret */
    private String providerSecret;

    /**
     * 企业授权回调url
     */
    private String authCallback;

    private List<AppConfig> appConfigs;

    @Getter
    @Setter
    public static class AppConfig {

        /** 企业的suiteId */
        private String suiteID;
        /**
         * 设置企业微信应用的AgentId
         */
        private Integer agentId;

        /**
         * 设置企业微信应用的Secret
         */
        private String secret;

        /**
         * 设置企业微信应用的token
         */
        private String token;

        /**
         * 设置企业微信应用的EncodingAESKey
         */
        private String aesKey;

        /**
         * 通讯录Secret
         */
        private String chatSecret;

        public String getChatSecret() {
            return chatSecret;
        }

        public AppConfig setChatSecret(String chatSecret) {
            this.chatSecret = chatSecret;
            return this;
        }

        public String getSuiteID() {
            return suiteID;
        }

        public AppConfig setSuiteID(String suiteID) {
            this.suiteID = suiteID;
            return this;
        }

        public Integer getAgentId() {
            return agentId;
        }

        public AppConfig setAgentId(Integer agentId) {
            this.agentId = agentId;
            return this;
        }

        public String getSecret() {
            return secret;
        }

        public AppConfig setSecret(String secret) {
            this.secret = secret;
            return this;
        }

        public String getToken() {
            return token;
        }

        public AppConfig setToken(String token) {
            this.token = token;
            return this;
        }

        public String getAesKey() {
            return aesKey;
        }

        public AppConfig setAesKey(String aesKey) {
            this.aesKey = aesKey;
            return this;
        }
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }

    public String getCorpId() {
        return corpId;
    }

    public WxCpProperties setCorpId(String corpId) {
        this.corpId = corpId;
        return this;
    }

    public List<AppConfig> getAppConfigs() {
        return appConfigs;
    }

    public WxCpProperties setAppConfigs(
        List<AppConfig> appConfigs) {
        this.appConfigs = appConfigs;
        return this;
    }

    public String getProviderSecret() {
        return providerSecret;
    }

    public WxCpProperties setProviderSecret(String providerSecret) {
        this.providerSecret = providerSecret;
        return this;
    }

    public String getAuthCallback() {
        return authCallback;
    }

    public WxCpProperties setAuthCallback(String authCallback) {
        this.authCallback = authCallback;
        return this;
    }
}
