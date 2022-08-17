package com.yimsheng.matchingsystem.service.impl;

import com.yimsheng.matchingsystem.service.MatchingService;
import com.yimsheng.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public final static MatchingPool matchingpool=new MatchingPool();
    @Override
    public String addPlayer(Integer userId, Integer rating) {
        matchingpool.addPlayer(userId,rating);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        matchingpool.removePlayer(userId);
        return "remove player success";
    }
}
