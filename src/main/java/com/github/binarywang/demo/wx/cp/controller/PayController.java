package com.github.binarywang.demo.wx.cp.controller;

import com.dayi.common.util.BizResult;
import com.github.binarywang.demo.wx.cp.component.LoginComponent;
import com.github.binarywang.demo.wx.cp.component.WxParam;
import com.github.binarywang.demo.wx.cp.component.WxPermanentCode;
import com.monda.base.pay.vo.BasePayOrderResultVo;
import com.monda.base.pay.vo.PayOrderVo;
import com.monda.fl.pay.service.FlThirdPayService;
import com.monda.wechatpay.v3.enums.WechatPayType;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wut
 * @since 2022-05-12
 */
@Controller
@RequestMapping("/pay")
public class PayController {
    protected final static Logger logger = LoggerFactory.getLogger(PayController.class);

    @Resource
    private WxParam wxParam;
    @Resource
    private LoginComponent loginComponent;

    @Resource
    private FlThirdPayService flThirdPayService;

    @Resource
    private WxPermanentCode wxPermanentCode;

    @ResponseBody
    @GetMapping("/callPay")
    public BizResult pay(HttpServletRequest request) {

        String openId = loginComponent.getOpenId(wxPermanentCode.getCorp("company1")); // todo 拿到openId
        String ip = getIPAddress(request);

        PayOrderVo payOrderVo = new PayOrderVo("account1", "orderGroup24", "产品a", BigDecimal.valueOf(100),
            BigDecimal.valueOf(0), openId, ip, "http://q3s1222063.wicp.vip:29082/pay/succ", 1);
        payOrderVo.setWechatPayType(WechatPayType.JSAPI);
        payOrderVo.setBankType(1);
        ArrayList<PayOrderVo.Item> items = new ArrayList<>();
        items.add(new PayOrderVo.Item("orderGroup2_1", BigDecimal.valueOf(100), BigDecimal.valueOf(0)));
        payOrderVo.setItems(items);

        return flThirdPayService
            .createOrder("paypay123454", payOrderVo);
    }

    @PostMapping("/succ")
    public void paySucc(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String wechatpaySignature = request.getHeader("Wechatpay-Signature");
        String wechatpaySerial = request.getHeader("Wechatpay-Serial");
        String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");

        String body = getRequestBody(request);

        logger.info("收到微信支付回调, wechatpayNonce:{}, body:{}", wechatpayNonce, body);

        // 给微信一个好的响应
        String jsonStr = "{\"code\": \"SUCCESS\",\"message\": \"成功\"}";
        writeResponseStream(response, jsonStr);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取请求Body
     *
     * @param request
     * @return
     */
    private String getRequestBody(HttpServletRequest request) {
        String line = "";
        StringBuilder body = new StringBuilder(line);
        try {
            InputStream stream = request.getInputStream();
            // 读取POST提交的数据内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    /**
     * 输出响应流给微信
     *
     * @param response
     * @param json
     * @throws IOException
     */
    private void writeResponseStream(HttpServletResponse response, String json) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(json.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
