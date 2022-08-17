package com.yimsheng.backend.Service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yimsheng.backend.Service.user.account.RegisterService;
import com.yimsheng.backend.mapper.UserMapper;
import com.yimsheng.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String,String> map = new HashMap<>();
        if(username==null){
            map.put("error_message","username cannot be empty");
            return map;
        }
        if(password==null){
            map.put("error_message","password cannot be empty");
            return map;
        }
        username=username.trim();
        if(username.length()==0){
            map.put("error_message","username cannot be empty");
            return map;
        }

        if(password.length()==0||confirmedPassword.length()==0){
            map.put("error_message","password cannot be empty");
            return map;
        }

        if(username.length()>100){
            map.put("error_message","username length cannot be over 100");
            return map;
        }
        if(password.length()>100||confirmedPassword.length()>100){
            map.put("error_message","password length cannot be over 100");
            return map;
        }
        if(!password.equals(confirmedPassword)){
            map.put("error_message","password not compatible");
            return map;
        }
        //database query
        //to find username same as given one
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<User> users = userMapper.selectList(queryWrapper);

        if(!users.isEmpty()){
            map.put("error_message","already exist");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://i02piccdn.sogoucdn.com/48981c3dde37b2c9";
        User user = new User(null,username,encodedPassword,photo,1500);
        userMapper.insert(user);
        map.put("error_message","success");
        return map;
    }
}
