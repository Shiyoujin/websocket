package com.example.websocket.bean;

import lombok.Data;

@Data
public class SocketMsg {

    //消息类型 0所有人在的聊天室群聊 1好友互聊 2单独创建聊天室实现群聊
    private int type;

    private String fromUser;

    private String toUser;

    //发送消息内容
    private String msg;


}
