package com.example.smsmanager.bean;

import java.util.List;

/**
 * Created by admin on 2017/3/6.
 * eventbus消息传递
 */

public class MessageEvent<T>
{
    T  msg;
    List<T> list;
    public MessageEvent(T msg ){
        this.msg=msg;
    }
    public MessageEvent( List<T> list){
        this.list=list;
    }
    public T getMsg() {
        return msg;
    }

    public void setMsg(T msg) {
        this.msg = msg;
    }



    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
