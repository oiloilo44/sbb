package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @RequestMapping("/sbb")
    @ResponseBody
    public String index() {
        return "안녕하세요 sbb에 오신것을 환영합니다.";
    }

    @RequestMapping("/")
    public String root() {
//        return "redirect:/question/list"; //해당 페이지로 바로 이동
        return "forward:/question/list"; //url은 변경하지 않고 해당 페이지로 이동
    }
}