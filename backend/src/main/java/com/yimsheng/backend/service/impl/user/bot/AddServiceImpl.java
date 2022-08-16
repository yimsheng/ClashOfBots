package com.yimsheng.backend.Service.impl.user.bot;

import com.yimsheng.backend.mapper.BotMapper;
import com.yimsheng.backend.pojo.Bot;
import com.yimsheng.backend.pojo.User;
import com.yimsheng.backend.Service.impl.utils.UserDetailsImpl;
import com.yimsheng.backend.Service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public Map<String, String> add(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title=data.get("title");
        String description=data.get("description");
        String content=data.get("content");

        Map<String,String> map  = new HashMap<>();

        if(title==null||title.length()==0){
            map.put("error_message","title cannot be empty");
            return map;
        }
        if(title.length()>100){
            map.put("error_message","title is longer than 100");
            return map;
        }
        if (description == null || description.length() == 0) {
            map.put("error_message","This user did not write anything");
        }
        if(description.length()>300){
            map.put("error_message","description is longer than 300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("error_message","code cannot be empty");
            return map;
        }

        if(content.length()>10000){
            map.put("error_message","code cannot be longer than 100000");
            return map;
        }
        Date now=new Date();
        Bot bot = new Bot(null,user.getId(),title,description,content,now,now);

        botMapper.insert(bot);
        map.put("error_message","success");
        return map;
    }
}
