package com.github.binarywang.demo.wx.cp.controller;

import java.io.FileInputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wut
 * @since 2022-05-05
 */
@Controller
public class VerifyController {

    @GetMapping("/WW_verify_7uGOIjzhee608hHf.txt")
    public void get(HttpServletResponse response) {
        String file = "E:\\weixin-java-cp-demo-master\\src\\main\\resources\\WW_verify_7uGOIjzhee608hHf.txt";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String diskfilename = "WW_verify_7uGOIjzhee608hHf.txt";
//            response.setContentType("video/avi");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"");
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", "" + Integer.valueOf(data.length - 1));
            response.setHeader("Accept-Ranges", "bytes");
//            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            OutputStream os = response.getOutputStream();

            os.write(data);
            //先声明的流后关掉！
            os.flush();
            os.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
