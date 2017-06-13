package com.example.smsmanager.bean;

import java.io.Serializable;

/**
 * Created by admin on 2017/2/28.
 * 联系人实体类
 */

public class ContactsInfo implements Serializable{
    int cid;
    String name;
    String phone;
    String sendPhone;
    boolean isChoosed=false;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSendPhone() {
        return sendPhone;
    }

    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
