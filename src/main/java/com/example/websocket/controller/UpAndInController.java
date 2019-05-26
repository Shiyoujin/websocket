package com.example.websocket.controller;

import com.example.websocket.service.UpAndInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

/**
 * @author white matter
 */
@RestController
public class UpAndInController {
    @Autowired
    private UpAndInService upAndInService;

    @GetMapping(value = "signUp",produces = "application/json")
    public String signUp(String u_id,String u_name,String u_pass) {
        if (upAndInService.signUp(u_id,u_name,u_pass)){
            return "注册成功";
        }else {
            return "注册失败";
        }
    }

    @GetMapping(value = "login",produces = "application/json")
    public String signIn(String u_id, String u_pass, HttpServletRequest request){
        if (upAndInService.login(u_id,u_pass)){
            //加入session，增加验证。
            request.getSession().setAttribute("u_id",u_id);
            return "登录成功";
        }else {
            return "账号或者密码错误";
        }
    }



}
