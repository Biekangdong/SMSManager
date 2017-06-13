package com.example.smsmanager.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/10.
 * 文件实体类
 */

public class FileInfo implements Serializable {
    String fileName;
    String filesize;
    String fileTime;
    String filePath;
    Bitmap thumbnail;
    boolean isChoosed=false;
    long filesizeSmall;//单位为B的文件大小
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

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getFilesizeSmall() {
        return filesizeSmall;
    }

    public void setFilesizeSmall(long filesizeSmall) {
        this.filesizeSmall = filesizeSmall;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
