package com.github.binarywang.demo.wx.cp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wut
 * @since 2022-05-12
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping("/index")
    public String index() {
        return "test";
    }
}
