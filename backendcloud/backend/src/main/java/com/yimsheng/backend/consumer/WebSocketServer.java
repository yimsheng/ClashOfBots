package com.yimsheng.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.yimsheng.backend.consumer.utils.Game;
import com.yimsheng.backend.consumer.utils.JwtAuthentication;
import com.yimsheng.backend.mapper.RecordMapper;
import com.yimsheng.backend.mapper.UserMapper;
import com.yimsheng.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    private Session session = null;
    private User user;
    private Game game=null;
    private final static String addPlayerUrl="http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl="http://127.0.0.1:3001/player/remove/";

    //不是单例模式不能直接autowired，要搞成static做全局然后用
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;
    @Autowired
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper=userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper=recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate=restTemplate;
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

        }
    }

    public static void startGame(Integer aId, Integer bId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);
        Game game = new Game(13, 14, 25,a.getId(),b.getId());
        game.createMap();

        if(users.get(a.getId())!=null) {
            users.get(a.getId()).game = game;
        }
        if(users.get(b.getId())!=null) {
            users.get(b.getId()).game = game;
        }
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
        if(users.get(a.getId())!=null) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }
        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(users.get(b.getId())!=null) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }

    }

    private void startMatching(){
    //向matchingsystem发起请求
        System.out.println("Start matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl,data,String.class);//第三个参数是返回值的类
    }

    private void stopMatching(){
    //向matchingsystem发请求
        System.out.println("Stop matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);//第三个参数是返回值的类
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

