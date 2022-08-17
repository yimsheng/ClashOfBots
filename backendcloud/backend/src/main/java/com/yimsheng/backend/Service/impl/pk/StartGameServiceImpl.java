package com.yimsheng.backend.Service.impl.pk;

import com.yimsheng.backend.Service.pk.StartGameService;
import com.yimsheng.backend.consumer.WebSocketServer;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer bId) {
        System.out.println("start game: "+aId+" "+bId);
        WebSocketServer.startGame(aId, bId);
        return "start game success";
    }

}
