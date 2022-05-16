package com.github.binarywang.demo.wx.cp.wxmessage;

import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;

/**
 * 直接返回success字符串的消息
 * @author wut
 * @since 2022-05-05
 */
public class SuccessOutMessage extends WxCpXmlOutMessage {

    @Override
    public String toEncryptedXml(WxCpConfigStorage wxCpConfigStorage) {

        return "success";
    }
}
