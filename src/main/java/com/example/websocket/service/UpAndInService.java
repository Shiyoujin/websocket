package com.example.websocket.service;

import com.example.websocket.bean.Friends;
import com.example.websocket.bean.User;
import com.example.websocket.mapper.AllMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author white matter
 */
@Service
public class UpAndInService {
    @Autowired
    private AllMapper allMapper;

    public boolean signUp(String u_id,String u_name,String pass){
        User user = new User();
        user.setU_id(u_id);
        user.setU_name(u_name);
        user.setU_pass(pass);
        if (!check(u_id)){
            System.out.println("该用户已经存在");
            return false;
        } else {
            System.out.println("注册成功");
            return allMapper.signUp(user);
        }
    }

    public boolean check(String u_id){
        if (allMapper.Check(u_id)==0){
            return true;
        }else {
            return false;
        }
    }

    public boolean login(String u_id,String pass){
        User user1 = new User();
        user1.setU_id(u_id);
        user1.setU_pass(pass);
        if (allMapper.login(user1)!=0){
            return true;
        }else {
            return false;
        }
    }

}
