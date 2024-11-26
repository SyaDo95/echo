package com.echoproject.echo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping(value = {"/{path:^(?!api).*}", "/{path:^(?!api).*}/**"})
    public String redirect() {
        return "forward:/index.html";
    }
}


