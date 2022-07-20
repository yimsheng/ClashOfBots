package com.yimsheng.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimsheng.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;
//一个表对应一个pojo对应一个mapper
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
