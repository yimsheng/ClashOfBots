package com.yimsheng.backend.controller.pk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    //pk下的index子目录
    @RequestMapping("/")
    public String index(){
        //返回一个对应的html页面的路径
        return "pk/index.html";
    }
}
