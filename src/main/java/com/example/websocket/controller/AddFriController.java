package com.example.websocket.controller;

import com.example.websocket.service.AddFriService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author white matter
 */
@RestController
public class AddFriController {
    @Autowired
    private AddFriService addFriService;

    @GetMapping(value = "addFriend",produces ="application/json")
    public String addFriend(String u_id,String f_id){
        if (addFriService.addFriends(u_id,f_id)){
            return "添加好友成功";
        }else {
            return "添加失败！！！";
        }
    }

    @GetMapping(value = "checkFri",produces = "application/json")
    public String checkFri(String u_id,String f_id){
        if (addFriService.checkFri(u_id,f_id)){
            return "已是好友";
        }else {
            return "还未添加好友！！！";
        }
    }
}
