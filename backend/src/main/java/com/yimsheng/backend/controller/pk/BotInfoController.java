package com.yimsheng.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    @RequestMapping("getbotinfo/")
    //visit-> localhost:8080/pk/getbotinfo/
    public String getBotInfo(){
        return "I am the info";
    }
}
