package com.example.websocket.util;
import com.example.websocket.bean.Friends;
import com.example.websocket.bean.SocketMsg;
import com.example.websocket.service.AddFriService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author white matter
 */
@ServerEndpoint(value = "/websocket/{nickname}/{u_id}/{f_id}")
@Component
public class WebSocketServer {
   /**
   静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    */
    private static int onlineCount = 0;

    @Autowired
    private AddFriService addFriService;

    private String nickename;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //用u_id和该session进行绑定
    private static Map<String,Session> map = new HashMap<String,Session>();

    private static Map<String, List<String>> roomMap= new HashMap<String, List<String>>();

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //private static CopyOnWriteArraySet<String,WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    private static Map<String, WebSocketServer> clients = new ConcurrentHashMap<String, WebSocketServer>();


    //接受u_id
    private String u_id = "";

    //好友的id
    private String f_id = "";


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onopen(Session session, @PathParam("nickname") String nickename,@PathParam("f_id") String f_id,@PathParam("u_id") String u_id,@PathParam("roomNumber") String roomNumber) {
        this.session = session;
        //这里的nickname可以根据u_id查找数据库可得也可以通过session
        this.nickename = nickename;
        this.u_id = u_id;
        System.out.println(session.getId());

        int i = 0;

        for (String key : roomMap.keySet()){
            //之前有人创建房间号，加入进去
            if(key.equals(roomNumber)){
                roomMap.get(key).add(u_id);
                i = 1;
                break;
            }
        }

        //第一次创建房间号
        if (i==0){
                List<String> list = new ArrayList<String>();
                list.add(u_id);
                roomMap.put(roomNumber,list);
        }


        //用u_id和该session进行绑定
        map.put(u_id,session);

        clients.put(u_id,this);//加入set

        System.out.println("有新连接加入！当前在线人数为" + clients.size());

        this.session.getAsyncRemote().sendText("恭喜"+nickename+"成功连接上WebSocket(其频道号："+
                session.getId()+")-->当前在线人数为："+clients.size());

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        clients.remove(this);//从set中删除
        //subOnlineCount();//在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + clients.size());

        }


   /**
    * 收到客户端消息后调用的方法
    * @param messageAll 客户端发送过来的消息
    */
   @OnMessage
   public void onMessage(String messageAll, Session session,@PathParam("nickname") String nickename, @PathParam("u_id") String u_id, @PathParam("f_id")String f_id, @PathParam("roomNumber") String roomNumber) throws ServletException {
       System.out.println("来自客户端的消息-->:" + nickename + ":"+ messageAll);
       System.out.println(messageAll);
       //从客户端传过来的数据是json数据，所以这里使用jackson进行转换为SocketMsg对象，
       // 然后通过socketMsg的type进行判断是单聊还是群聊，进行相应的处理:
       //这里也可以用 JSONObject jsonObject = JSON.parseObject(messageAll) jsonObject.getString("toUser") 来转换
       ObjectMapper objectMapper = new ObjectMapper();
       //用来接受客户端传过来的所有信息
       SocketMsg socketMsg;

       //发送图片需要
       HttpServletRequest request = null;

       try {
           socketMsg = objectMapper.readValue(messageAll,SocketMsg.class);
           if (socketMsg.getType() == 0){
               //所有人所在的一个聊天室，群发消息
               broadcast(nickename + ": " + socketMsg.getMsg(),request);
           } else if (socketMsg.getType()==1){
               Session fromSession = map.get(socketMsg.getFromUser());
               Session toSession = map.get(socketMsg.getToUser());
               //先检验是否添加为好友
               if (addFriService.checkFri(u_id,f_id)){
                   //两个好友间私聊，需要找到发送者和接收者
                   socketMsg.setFromUser(session.getId());//发送者
                   //两个好友之间的私聊
                   if (toSession !=null){
                       sendMessageTo(nickename + ": "+socketMsg.getMsg(),u_id,f_id);
                   }else {
                       fromSession.getAsyncRemote().sendText( "系统消息：对方当前尚未在线！");
                   }
               } else {
                   fromSession.getAsyncRemote().sendText( "系统消息：你当前并未和他/她成为好友，不能发送消息！");
               }
           } else {//这里的 Type==2
               if (!roomNumber.equals("")) {
                   //给房间内的所有人发送消息
                   sendRoom(nickename + ": " + socketMsg.getMsg(), roomNumber);
               }else {
                   session.getAsyncRemote().sendText("系统消息：房间号为空！");
               }
           }
       } catch (JsonParseException e) {
           e.printStackTrace();
       } catch (JsonMappingException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    /**
     * * 发生错误时调用
     **
     *     
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    /**
     * 所有人在一个聊天室时，群发自定义的消息
     * 这里只在所有中实现发送图片，马上1点了，明天满课该睡觉了。。。
     */
    public void broadcast(String message,HttpServletRequest request) throws IOException, ServletException {

        for (WebSocketServer item : clients.values()){
            //同步异步说明参考：http://blog.csdn.net/who_is_xiaoming/article/details/53287691
            // this.session.getBasicRemote().sendText(message);
            item.session.getAsyncRemote().sendText(message);//异步发送消息.
        }

        Part part = request.getPart("photo");

        String filename = getFilename(part);
        //这里获得的url应该存数据库,相应的前端可以取出来然后展示到前端去
        String url = "../img/" + filename;

    }

    /**
     * 单独与好友私聊
     * @param message
     * @param f_id
     * @throws IOException
     */
    public void sendMessageTo(String message,String u_id,String f_id) throws IOException {
        for (WebSocketServer item : clients.values()) {
            if (item.u_id.equals(f_id) || item.u_id.equals(u_id)) {
                //这里要给自己和好友都发消息
                item.session.getAsyncRemote().sendText(message);
            }
        }
    }

    /**
     * 单独创建房间，几个人一起聊天，房间内的人都能看到消息
     * @param message
     * @param roomNumber
     */
    public void sendRoom(String message,String roomNumber){
        List<String> list = new ArrayList<>();
        list = roomMap.get(roomNumber);
        //给房间里的所有人发送消息，让单独的房间所有人都能看见
        for (String  s : list ){
            clients.get(s).session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * 发送图片
     * @param part
     * @return
     */
        public String getFilename(Part part){
        String header = part.getHeader("Content-Disposition");
        String filename = header.substring(header.indexOf("filename=\"") + 10,header.lastIndexOf("\""));
        return filename;
    }


}
