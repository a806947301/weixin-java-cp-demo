package com.github.binarywang.demo.wx.cp.component;

import com.github.binarywang.demo.wx.cp.component.WxPermanentCode.Corp;
import com.github.binarywang.demo.wx.cp.config.WxCpProperties;
import com.github.binarywang.demo.wx.cp.utils.WxHttpUtil;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author wut
 * @since 2022-05-05
 */
@Component
public class Department {

    @Resource
    private WxParam wxParam;
    @Resource
    WxPermanentCode wxPermanentCode;
    @Resource
    WxCpProperties wxCpProperties;

    public String getDepartment1(Corp corp) {
//        String chatSecret = wxCpProperties.getAppConfigs().get(0).getChatSecret();
        String token = WxHttpUtil.getTongToken(corp, corp.getCharSecret());

        String department = WxHttpUtil.getDepartment(token);
        if (Objects.isNull(department)) {
            return "出现了问题";
        }
        return WxHttpUtil.getPersons(department, token);

    }


    public String getDepartment(Corp corp) {
        String token = WxHttpUtil.getAccessToken(wxParam.getSuiteAccessToken(), wxPermanentCode.getCorp("company1"));

        String department = WxHttpUtil.getDepartment(token);
        if (Objects.isNull(department)) {
            return "出现了问题";
        }
        return WxHttpUtil.getPersons(department, token);

    }
}
