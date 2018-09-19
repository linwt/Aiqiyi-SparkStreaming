package com.example.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWeb {

    @ResponseBody
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "hello spring boot!";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ModelAndView firstDemo() {
        return new ModelAndView("test");
    }

}
