package com.github.binarywang.demo.wx.cp.utils;

import com.github.binarywang.demo.wx.cp.component.WxPermanentCode.Corp;
import com.github.binarywang.demo.wx.cp.utils.HttpClient.HttpResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author wut
 * @since 2022-05-05
 */
public class WxHttpUtil {
    protected final static Logger logger = LoggerFactory.getLogger(WxHttpUtil.class);

    /**
     * 获取服务商token
     *
     * @param suiteId
     * @param suiteSecret
     * @param suiteTicket
     * @return
     */
    public static String getSuiteToken(String suiteId, String suiteSecret, String suiteTicket) {
        HashMap<String, String> body = new HashMap<>();
        body.put("suite_id", suiteId);
        body.put("suite_secret", suiteSecret);
        body.put("suite_ticket", suiteTicket);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson("https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token?debug=1", body);
            logger.info("getSuiteToken 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("suite_access_token");
            }
        } catch (Exception e) {
            logger.error("http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getSuiteToken出了点问题");
        return null;
    }

    /**
     * 获取预授权码
     * @param suiteAccessToken
     * @return
     */
    public static String getPreAuthCode(String suiteAccessToken) {
        HashMap<String, String> params = new HashMap<>();
        params.put("suite_access_token", suiteAccessToken);
        try {
            HttpResult result = HttpClient.httpGet("https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code", params);
            logger.info("getPreAuthCode 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("pre_auth_code");
            }
        } catch (Exception e) {
            logger.error("getPreAuthCode，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getSuiteToken出了点问题");
        return null;
    }

    public static void setPermission(String preAuthCode, String suiteAccessToken) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/set_session_info?suite_access_token=" + suiteAccessToken + "&debug=1";

        HashMap<String, Object> body = new HashMap<>();
        body.put("pre_auth_code", preAuthCode);
        HashMap<String, Integer> bmap = new HashMap<>();
        bmap.put("auth_type", 1);
        body.put("session_info", bmap);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("setPermission 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return;
            }
        } catch (Exception e) {
            logger.error("http请求错误");
            e.printStackTrace();
            return;
        }

        logger.error("setPermission出了点问题");
        return;
    }

    /**
     * 获取永久授权码
     * @param authCode
     * @return
     */
    public static Corp getPermanentCorp(String authCode, String suiteAccessToken) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=SUITE_ACCESS_TOKEN";
        url = url.replaceAll("SUITE_ACCESS_TOKEN", suiteAccessToken);

        HashMap<String, String> body = new HashMap<>();
        body.put("auth_code", authCode);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("getPermanentCorp 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                String code = jsonObject.getString("permanent_code");
                String corpId = jsonObject.getJSONObject("auth_corp_info").getString("corpid");
                Integer agentId = jsonObject.getJSONObject("auth_info").getJSONArray("agent").getJSONObject(0).getInt("agentid");
                Corp corp = new Corp().setCode(code).setCorpId(corpId).setAgentId(agentId);
                return corp;
            }
        } catch (Exception e) {
            logger.error("http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getSuiteToken出了点问题");
        return null;
    }

    public static String getTongToken(Corp corp, String secret) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        HashMap<String, String> body = new HashMap<>();
        body.put("corpid", corp.corpId);
        body.put("corpsecret", secret);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpGet(url, body);
            logger.info("getTongToken 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                String token = jsonObject.getString("access_token");
                return token;
            }
        } catch (Exception e) {
            logger.error("http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getTongToken出了点问题");
        return null;

    }

    /**
     * 获取企业的token
     * @param suiteAccessToken
     * @param corp
     * @return
     */
    public static String getAccessToken(String suiteAccessToken, Corp corp) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN";
        url = url.replaceAll("SUITE_ACCESS_TOKEN", suiteAccessToken);

        HashMap<String, String> body = new HashMap<>();
        body.put("auth_corpid", corp.corpId);
        body.put("permanent_code", corp.code);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("getAccessToken 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                String token = jsonObject.getString("access_token");
                return token;
            }
        } catch (Exception e) {
            logger.error("http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getAccessToken出了点问题");
        return null;

    }

    public static String getDepartment(String accessToken) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//            .queryParam("access_token", accessToken);
//        url = builder.toUriString();

//        url = url.replaceAll("ACCESS_TOKEN", accessToken);

        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        try {
            HttpResult result = HttpClient.httpGet(url, params);
            logger.info("getDepartment 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                JSONArray departments = jsonObject.getJSONArray("department");
                JSONObject dep = departments.getJSONObject(0);
                return dep.getString("id");
            }
        } catch (Exception e) {
            logger.error("getDepartment，http请求错误");
            e.printStackTrace();
            return null;
        }
        logger.error("getDepartment出了点问题");
        return null;
    }

    public static String getPersons(String departmentId, String accessToken) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list";
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("department_id", departmentId);
        params.put("fetch_child", "0");
        try {
            HttpResult result = HttpClient.httpGet(url, params);
            logger.info("getDepartment 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getPersons，http请求错误");
            e.printStackTrace();
            return null;
        }
        logger.error("getPersons出了点问题");
        return null;
    }

    /**
     * 获取服务商的Provider_Secret
     * @param corpId
     * @param providerSecret
     * @return
     */
    public static String getProviderSecret(String corpId, String providerSecret) {
        HashMap<String, String> body = new HashMap<>();
        body.put("corpid", corpId);
        body.put("provider_secret", providerSecret);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson("https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token", body);
            logger.info("getProviderSecret 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("provider_access_token");
            }
        } catch (Exception e) {
            logger.error("getProviderSecret http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getProviderSecret 出了点问题");
        return null;
    }

    public static String getLoginUserInfo(String providerAccessToken, String authCode) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token=" + providerAccessToken;

        HashMap<String, String> body = new HashMap<>();
        body.put("auth_code", authCode);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("getLoginUserInfo 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getLoginUserInfo http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getLoginUserInfo 出了点问题");
        return null;
    }

    public static String getLoginUserInfoH5(String suiteAccessToken, String code) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd";

        HashMap<String, String> body = new HashMap<>();
        body.put("suite_access_token", suiteAccessToken);
        body.put("code", code);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpGet(url, body);
            logger.info("getLoginUserInfoH5 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getLoginUserInfoH5 http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getLoginUserInfoH5 出了点问题");
        return null;
    }

    public static String getLoginUserDetailH5(String suiteAccessToken, String userTicket) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserdetail3rd?suite_access_token=" + suiteAccessToken;

        HashMap<String, String> body = new HashMap<>();
        body.put("user_ticket", userTicket);
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("getLoginUserDetailH5 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getLoginUserDetailH5 http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getLoginUserDetailH5 出了点问题");
        return null;
    }

    public static String getPerson(String accessToken, String userId) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("userid", userId);
        try {
            HttpResult result = HttpClient.httpGet(url, params);
            logger.info("getPerson 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getPerson，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getPerson 出了点问题");
        return null;
    }

    public static String getAdminList(String suiteAccessToken, String corpId, Integer agentId) {
        String url = " https://qyapi.weixin.qq.com/cgi-bin/service/get_admin_list?suite_access_token=" + suiteAccessToken;
        HashMap<String, Object> params = new HashMap<>();
        params.put("auth_corpid", corpId);
        params.put("agentid", agentId);
        try {
            HttpResult result = HttpClient.httpPostJson(url, params);
            logger.info("getAdminList 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getAdminList，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getAdminList 出了点问题");
        return null;
    }

    /***
     * 上传通讯录转换文件
     * @param providerAccessToken
     * @param uploadFile
     * @return media_id
     */
    public static String uploadTongTranFile(String providerAccessToken, File uploadFile) {

        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/media/upload?provider_access_token=" + providerAccessToken
            +"&type=file";
        try {
            HttpResult result = HttpClient.doPostFile(url, uploadFile);
            logger.info("getAdminList 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("media_id");
            }
        } catch (Exception e) {
            logger.error("getAdminList，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("uploadFile 出了点问题");
        return null;
    }

    /**
     * 异步通讯录id转译
     * @param providerAccessToken
     * @param mediaId
     * @param corpId
     * @return
     */
    public static String tranTong(String providerAccessToken, String mediaId, String corpId) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/contact/id_translate?provider_access_token=" + providerAccessToken;
        HashMap<String, Object> body = new HashMap<>();
        body.put("auth_corpid", corpId);
        body.put("media_id_list", Arrays.asList(mediaId));
        body.put("output_file_name", "tong");
        body.put("output_file_format", "pdf");
        HttpResult result = null;
        try {
            result = HttpClient
                .httpPostJson(url, body);
            logger.info("tranTong 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("jobid");
            }
        } catch (Exception e) {
            logger.error("tranTong http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("tranTong 出了点问题");
        return null;
    }

    public static String getTaskResult(String providerAccessToken, String jobId) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/batch/getresult";
        HashMap<String, String> params = new HashMap<>();
        params.put("provider_access_token", providerAccessToken);
        params.put("jobid", jobId);
        try {
            HttpResult result = HttpClient.httpGet(url, params);
            logger.info("getTaskResult 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return result.content;
            }
        } catch (Exception e) {
            logger.error("getTaskResult，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getTaskResult 出了点问题");
        return null;
    }

    /**
     * 获取用户的openId
     * @param accessToken 企业的token
     * @param userId    用户的userId
     * @return
     */
    public static String getOpenId(String accessToken, String userId) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=" + accessToken;

        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userId);
        try {
            HttpResult result = HttpClient.httpPostJson(url, params);
            logger.info("getOpenId 收到http响应，内容：{}", result);
            JSONObject jsonObject = new JSONObject(result.content);
            if (!jsonObject.has("errcode") || jsonObject.getInt("errcode") == 0) {
                return jsonObject.getString("openid");
            }
        } catch (Exception e) {
            logger.error("getOpenId，http请求错误");
            e.printStackTrace();
            return null;
        }

        logger.error("getOpenId 出了点问题");
        return null;
    }

    public static String getPersonOpenId() {
        return null;
    }
}
