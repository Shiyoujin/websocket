package com.example.websocket.mapper;

import com.example.websocket.bean.Friends;
import com.example.websocket.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.websocket.server.PathParam;

/**
 * @author white matter
 */
@Mapper
@Component
public interface AllMapper {

    @Insert("insert into user(u_id,u_name,u_pass) value (#{u_id},#{u_name},#{u_pass})")
    boolean signUp(User user);

    @Select("select ifnull((select u_id from user where u_id=#{u_id}),0)")
    int Check(String u_id);

    @Select("select ifnull((select u_id from user where u_id=#{u_id} and u_pass=#{u_pass}),0)")
    int login(User user);

    @Insert("insert into Friends(u_id,u_name,f_id,f_name) value (#{u_id},#{u_name},#{f_id},#{f_name})")
    boolean addFriends(Friends friends);

    @Select("select * from user where u_id=#{u_id}")
    User inquiryUser(String u_id);

    @Select("select ifnull((select u_id from friends where (u_id=#{u_id} and f_id=#{f_id}) or (u_id=#{f_id} and f_id=#{u_id})),0)")
    int checkFri(@Param("u_id") String u_id, @Param("f_id") String f_id);
















}
