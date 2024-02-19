package cn.com.duiba.tuia.engine.activity.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sunjiangrong .
 * 17/6/23.
 */
@Controller
@RequestMapping("/23pic")
public class PicController extends BaseController{

    @RequestMapping("/{picId}")
    public void getPic(@PathVariable("picId") String picId, HttpServletRequest request, HttpServletResponse response) {


        if (!"2017062726465".equals(picId)) {
            return;
        }
        try {

            URL url = new URL("http://tuia.oss-cn-hangzhou.aliyuncs.com/tuia/234/26465.png");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            InputStream inStream = connection.getInputStream();

            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                byteOutStream.write(buffer, 0, len);
            }
            inStream.close();

            byte[] outData = byteOutStream.toByteArray();

            response.setContentType("image/jpg");
            OutputStream outStream = response.getOutputStream();
            outStream.write(outData);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            logger.warn("getPic error:{}", e);
        }
    }
}
