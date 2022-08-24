package com.yimsheng.backend.Service.impl.pk;

import com.yimsheng.backend.Service.pk.ReceiveBotMoveService;
import com.yimsheng.backend.consumer.WebSocketServer;
import com.yimsheng.backend.consumer.utils.Game;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        if(WebSocketServer.users.get(userId)!=null){
            Game game = WebSocketServer.users.get(userId).game;
            if(game!=null){
                if(game.getPlayerA().getId().equals(userId)){
                    game.setNextStepA(direction);
                }else if(game.getPlayerB().getId().equals(userId)){
                    game.setNextStepB((direction));
                }
            }
        }
        return "receive bot move success";
    }
}
