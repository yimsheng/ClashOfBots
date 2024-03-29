package com.yimsheng.backend.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yimsheng.backend.Service.impl.utils.UserDetailsImpl;
import com.yimsheng.backend.mapper.UserMapper;
import com.yimsheng.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new RuntimeException("User not exist");
        }
        return new UserDetailsImpl(user);
    }
}
