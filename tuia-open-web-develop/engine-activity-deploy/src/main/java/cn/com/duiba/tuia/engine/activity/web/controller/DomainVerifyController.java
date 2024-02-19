package cn.com.duiba.tuia.engine.activity.web.controller;

import cn.com.duiba.tuia.ssp.center.api.dto.DomainVerificationDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteDomainVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class DomainVerifyController {

    @Autowired
    private RemoteDomainVerificationService remoteDomainVerificationService;

    @RequestMapping(value = "/MP_verify_{key}.txt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String verify4WeChatPublicNo(@PathVariable String key) {
        return key;
    }

    @RequestMapping(value = "/{file}.txt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String verify4WeChatLittleGame(@PathVariable String file, HttpServletResponse response) {
        String content = "";
        List<DomainVerificationDto> dtoList = remoteDomainVerificationService.selectByFileName(file);
        if (!dtoList.isEmpty()) {
            content = dtoList.get(0).getContent();
        }
        return content;
    }

}
