package com.yimsheng.backend.Service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yimsheng.backend.Service.record.GetRecordListService;
import com.yimsheng.backend.mapper.RecordMapper;
import com.yimsheng.backend.mapper.UserMapper;
import com.yimsheng.backend.pojo.Record;
import com.yimsheng.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordIPage = new Page<>(page,10);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");//按时间顺序递减确保不同时
        List<Record> records = recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for(Record record:records){
            User userA  = userMapper.selectById(record.getAId());
            User userB  = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo",userA.getPhoto());
            item.put("a_username",userA.getUsername());
            item.put("b_photo",userB.getPhoto());
            item.put("b_username",userB.getUsername());
            String result="Draw";
            if ("A".equals(record.getLoser())) result="B wins";
            else if ("B".equals(record.getLoser())) result="A wins";
            item.put("result",result);
            item.put("record",record);
            items.add(item);
        }
        resp.put("records",items);
        resp.put("records_count",recordMapper.selectCount(null));//传一个总数方便前端
        return resp;
    }
}
