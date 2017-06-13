package com.example.smsmanager.bean;

import java.util.List;

/**
 * Created by admin on 2017/3/2.
 * 消息详情实体类
 */

public class SmsExpandInfo {
    String smsTitleId;
    String smsTitle;
    List<SmsInfo> smsInfos;

    public List<SmsInfo> getSmsInfos() {
        return smsInfos;
    }

    public void setSmsInfos(List<SmsInfo> smsInfos) {
        this.smsInfos = smsInfos;
    }

    public String getSmsTitle() {
        return smsTitle;
    }

    public void setSmsTitle(String smsTitle) {
        this.smsTitle = smsTitle;
    }

    public String getSmsTitleId() {
        return smsTitleId;
    }

    public void setSmsTitleId(String smsTitleId) {
        this.smsTitleId = smsTitleId;
    }
}
