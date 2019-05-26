package com.example.websocket.service;

import com.example.websocket.bean.Friends;
import com.example.websocket.bean.User;
import com.example.websocket.mapper.AllMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author white matter
 */
@Service
public class AddFriService {
    @Autowired
    private AllMapper allMapper;

//    public User inquiryUser(String u_id){
//        User user = new User();
//        user = allMapper.inquiryUser(u_id);
//        return user;
//    }

    public boolean addFriends(String u_id,String f_id) {
        User userMe = new User();
        User userFri = new User();
        Friends friends = new Friends();
        userMe = allMapper.inquiryUser(u_id);
        userFri = allMapper.inquiryUser(f_id);
        friends.setU_id(u_id);
        friends.setU_name(userMe.getU_name());
        friends.setF_id(f_id);
        friends.setF_name(userFri.getU_name());
        if (allMapper.addFriends(friends)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFri(String u_id,String f_id){
        if(allMapper.checkFri(u_id,f_id)!=0){
            return true;
        }else {
            return false;
        }
    }
}
