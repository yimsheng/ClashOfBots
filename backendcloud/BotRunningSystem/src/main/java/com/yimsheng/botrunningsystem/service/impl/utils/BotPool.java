package com.yimsheng.botrunningsystem.service.impl.utils;

import com.yimsheng.botrunningsystem.service.impl.utils.Bot;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition=lock.newCondition();//条件变量
    private final Queue<Bot> bots = new LinkedList<>();


    public void addBot(Integer userId,String botCode, String input){
        lock.lock();
        try{
            bots.add(new Bot(userId,botCode,input));
            condition.signalAll();
        }finally{
            lock.unlock();
        }
    }
    private void consume(Bot bot){
        //动态编译字符串里的java代码
        Consumer consumer=new Consumer();
        consumer.startTimeout(2000,bot);

    }
    @Override
    public void run() {
        while(true){
            lock.lock();
            if(bots.isEmpty()){
                try{
                   condition.await();
                }catch (InterruptedException e){
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                Bot bot = bots.remove();//取完队头就没事了可以开锁
                lock.unlock();
                consume(bot);//编译执行代码时间久
            }
        }
    }
}
