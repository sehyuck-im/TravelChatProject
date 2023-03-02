package com.TravelChat.common.cotroller;

import com.TravelChat.common.service.PhotoService;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {
    @Autowired
    private HttpSession session;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private MemberService ms;

    @RequestMapping("/")
    public String home() {

        return "index";
    }

    @GetMapping("/main")
    public String main() {

        return "/main/main";
    }
}
