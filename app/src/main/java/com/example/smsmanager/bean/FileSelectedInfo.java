package com.example.smsmanager.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/10.
 * 文件选中实体类
 */

public class FileSelectedInfo implements Serializable {
    String fileName;
    String filesize;
    String filePath;
    String fileTime;
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }
}
