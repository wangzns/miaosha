package com.sky.miaosha;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * description:
 * jdk: 1.8
 */
@Controller
public class RouteController {


    @RequestMapping("/createitem")
    public String createitem() {
        return "html/createitem.html";
    }


    @RequestMapping("/getitem")
    public String getitem() {
        return "html/getitem.html";
    }

    @RequestMapping("/getotp")
    public String getotp() {
        return "html/getotp.html";
    }

    @RequestMapping("/listitem")
    public String listitem() {
        return "html/listitem.html";
    }

    @RequestMapping("/login")
    public String login() {
        return "html/login.html";
    }

    @RequestMapping("/register")
    public String register() {
        return "html/register.html";
    }




}
