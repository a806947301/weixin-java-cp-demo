package com.github.binarywang.demo.wx.cp.handler;

import com.github.binarywang.demo.wx.cp.builder.TextBuilder;
import com.github.binarywang.demo.wx.cp.component.WxParam;
import com.github.binarywang.demo.wx.cp.utils.JsonUtils;
import com.github.binarywang.demo.wx.cp.wxmessage.SuccessOutMessage;
import javax.annotation.Resource;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
public class MsgHandler extends AbstractHandler {
    @Resource
    WxParam wxParam;

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
                                    WxSessionManager sessionManager) {
        final String msgType = wxMessage.getMsgType();
        if (msgType == null) {
            // 如果msgType没有，就自己根据具体报文内容做处理
            Object infoType = wxMessage.getAllFieldsMap().get("InfoType");
            if (infoType != null && infoType.equals("suite_ticket")) {
                return handleSuiteTicket(wxMessage);
            }
        }

        if (!msgType.equals(WxConsts.XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //TODO 组装回复消息
        String content = "收到信息内容：" + JsonUtils.toJson(wxMessage);

        return new TextBuilder().build(content, wxMessage, cpService);

    }


    private WxCpXmlOutMessage handleSuiteTicket(WxCpXmlMessage wxMessage) {

        Map<String, Object> map = wxMessage.getAllFieldsMap();
        String suiteTicket = (String) map.get("SuiteTicket");

        logger.info("收到发送SuiteTicker消息，把当前ticket设为：{}", suiteTicket);

        wxParam.setSuiteTicket(suiteTicket);
        return new SuccessOutMessage();
    }
}
