package com.yimsheng.backend.Service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yimsheng.backend.mapper.BotMapper;
import com.yimsheng.backend.pojo.Bot;
import com.yimsheng.backend.pojo.User;
import com.yimsheng.backend.Service.impl.utils.UserDetailsImpl;
import com.yimsheng.backend.Service.user.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GetListServiceImpl implements GetListService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public List<Bot> getList() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        QueryWrapper<Bot> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        return botMapper.selectList(queryWrapper);
    }
}
