package com.yimsheng.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.yimsheng.backend.consumer.utils.Game;
import com.yimsheng.backend.consumer.utils.JwtAuthentication;
import com.yimsheng.backend.mapper.RecordMapper;
import com.yimsheng.backend.mapper.UserMapper;
import com.yimsheng.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    //全局可见用来存储所有的链接,线程安全
    final public static ConcurrentHashMap<Integer,WebSocketServer> users=new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchpool=new CopyOnWriteArraySet<>();
    private Session session = null;
    private User user;
    private Game game=null;

    //不是单例模式不能直接autowired，要搞成static做全局然后用
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper=recordMapper;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session=session;
//        System.out.println("Connected");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user=userMapper.selectById(userId);
        if(this.user!=null) {
            users.put(userId, this);
        }else{
            this.session.close();
        }

    }

    @OnClose
    public void onClose() {
        // 关闭链接
//        System.out.println("Disconnected");
        if(this.user!=null){
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void startMatching(){
//        System.out.println("Start Matching!");
        matchpool.add(this.user);
        //temporarily matching logic
        while (matchpool.size() >= 2) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 25,a.getId(),b.getId());
            game.createMap();


            users.get(a.getId()).game=game;
            users.get(b.getId()).game=game;
            //启动一个线程
            game.start();

            //后端把两名玩家的信息传到前端
            //封装地图和两个玩家id和位置信息
            JSONObject respGame = new JSONObject();
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_xy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_xy",game.getPlayerB().getSy());
            respGame.put("map",game.getG());


            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game", respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game", respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }

    }

    private void stopMatching(){
//        System.out.println("Stop matching!!");
        matchpool.remove(this.user);
    }

    private void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
//        System.out.println("Received message");
        JSONObject data = JSONObject.parseObject(message);
        String event= data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        }else if("stop-matching".equals(event)){
            stopMatching();
        }else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        //后端向前端发送信息
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

