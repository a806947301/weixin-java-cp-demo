package com.github.binarywang.demo.wx.cp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@SpringBootApplication
@ImportResource("classpath:/dubbo-account.xml")
public class WxCpDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(WxCpDemoApplication.class, args);
  }
}
