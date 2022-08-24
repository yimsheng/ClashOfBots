package com.yimsheng.backend.Service.impl.pk;

import com.yimsheng.backend.Service.pk.StartGameService;
import com.yimsheng.backend.consumer.WebSocketServer;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer aBotId,Integer bId,Integer bBotId) {
        System.out.println("start game: "+aId+" "+aBotId+" "+bId+" "+bBotId);
        WebSocketServer.startGame(aId,aBotId, bId,bBotId);
        return "start game success";
    }

}
