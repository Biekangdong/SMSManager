package com.example.smsmanager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.SmsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/2.
 * 短信数据库操作类
 */
public class SmsDatabase {
    private final DatabaseHelper dbHelper;

    public SmsDatabase(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 增
     * 
     * @param data
     */
    public void insert(SmsInfo data) {
        String sql = "insert into " + DatabaseHelper.SMS_TABLE_NAME;
        sql += "(sendName,sendPhone, receiveName,receivePhone,smsContent,isRead,sendDate,sendTime,hidePhone,"
                +"fileName,fileSize,filePath,isSuccess,fileType) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new Object[] {data.getSendName() + "", data.getSendPhone() + "",
                data.getReceiveName() + "", data.getReceivePhone() + "",
                data.getSmsContent() + "",data.getIsRead() + "",data.getSendDate() + "",
                data.getSendTime() + "", data.getHidePhone()+ "",
                data.getFileName() + "",data.getFileSize()+ "", data.getFilePath()+ "",
                data.getIsSuccess()+"",data.getFileType()});
        sqlite.close();
    }

    /**
     * 删
     * 
     * @param id
     */
    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + DatabaseHelper.SMS_TABLE_NAME + " where sid=?");
        sqlite.execSQL(sql, new Integer[] { id });
        sqlite.close();
    }

    /**
     * 更新导入文件的状态
     *
     * @param data
     * sid,sendPhone, receiveName,receivePhone,smsContent,sendDate
     */
    public void updateFileState(SmsInfo data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.SMS_TABLE_NAME + " set filePath=?,isSuccess=? where sid=?");
        sqlite.execSQL(sql,
                new String[] { data.getFilePath()+ "", data.getIsSuccess()+ "",data.getSid()+ ""});
        sqlite.close();
    }
    /**
     * 更新已读
     *
     * @param data
     * sid,sendPhone, receiveName,receivePhone,smsContent,sendDate
     */
    public void updateIsRead(SmsInfo data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.SMS_TABLE_NAME + " set isRead=? where sid=?");
        sqlite.execSQL(sql, new String[] {data.getIsRead() + "",data.getSid() + ""});
        Log.d("sql",sql);
        sqlite.close();
    }

    /**
     * 查
     * 
     * @param where
     * @return
     */
    public List<SmsInfo> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<SmsInfo> data = null;
        data = new ArrayList<SmsInfo>();
        String sql="select * from "
                + DatabaseHelper.SMS_TABLE_NAME + where;
        Cursor cursor = sqlite.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            SmsInfo smsInfo = new SmsInfo();
            smsInfo.setSid(cursor.getInt(0));
            smsInfo.setSendName(cursor.getString(1));
            smsInfo.setSendPhone(cursor.getString(2));
            smsInfo.setReceiveName(cursor.getString(3));
            smsInfo.setReceivePhone(cursor.getString(4));
            smsInfo.setSmsContent(cursor.getString(5));
            smsInfo.setIsRead(cursor.getString(6));
            smsInfo.setSendDate(cursor.getString(7));
            smsInfo.setSendTime(cursor.getString(8));
            smsInfo.setHidePhone(cursor.getString(9));
            smsInfo.setFileName(cursor.getString(10));
            smsInfo.setFileSize(cursor.getString(11));
            smsInfo.setFilePath(cursor.getString(12));
            smsInfo.setIsSuccess(cursor.getInt(13));
            smsInfo.setFileType(cursor.getString(14));
            data.add(smsInfo);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.d("smsDatabase---queryWhere",sql);
        return data;
    }
    /**
     * 查 sendDate
     *
     * @param where
     * @return
     */
    public List<SmsInfo> queryWhere(String where,String where2) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<SmsInfo> data = null;
        data = new ArrayList<SmsInfo>();
        String sql="select * from (select * from "
                + DatabaseHelper.SMS_TABLE_NAME + where+")"+where2;
        Cursor cursor = sqlite.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            SmsInfo smsInfo = new SmsInfo();
            smsInfo.setSid(cursor.getInt(0));
            smsInfo.setSendName(cursor.getString(1));
            smsInfo.setSendPhone(cursor.getString(2));
            smsInfo.setReceiveName(cursor.getString(3));
            smsInfo.setReceivePhone(cursor.getString(4));
            smsInfo.setSmsContent(cursor.getString(5));
            smsInfo.setIsRead(cursor.getString(6));
            smsInfo.setSendDate(cursor.getString(7));
            smsInfo.setSendTime(cursor.getString(8));
            smsInfo.setHidePhone(cursor.getString(9));
            smsInfo.setFileName(cursor.getString(10));
            smsInfo.setFileSize(cursor.getString(11));
            smsInfo.setFilePath(cursor.getString(12));
            smsInfo.setIsSuccess(cursor.getInt(13));
            smsInfo.setFileType(cursor.getString(14));
            data.add(smsInfo);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
       Log.d("smsDatabase---queryWhere",sql);
        return data;
    }
    /**
     * 查 phone
     *
     * @return 最后一条数据的id
     */
    public int queryWhereLastId() {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        String sql="select * from " + DatabaseHelper.SMS_TABLE_NAME + " order by sid desc limit 0,1";
        Cursor cursor = sqlite.rawQuery(sql, null);
        int sid =-1;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            sid= cursor.getInt(0);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.d("smsDatabase---queryWhereIsRead",sql);
        return sid;
    }
    /**
     * 查 phone
     *
     * @return 最后一条数据
     */
    public SmsInfo queryWhereLastData() {
        SmsInfo smsInfo = null;
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        String sql="select * from " + DatabaseHelper.SMS_TABLE_NAME + " order by sid desc limit 0,1";
        Cursor cursor = sqlite.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            smsInfo = new SmsInfo();
            smsInfo.setSid(cursor.getInt(0));
            smsInfo.setSendName(cursor.getString(1));
            smsInfo.setSendPhone(cursor.getString(2));
            smsInfo.setReceiveName(cursor.getString(3));
            smsInfo.setReceivePhone(cursor.getString(4));
            smsInfo.setSmsContent(cursor.getString(5));
            smsInfo.setIsRead(cursor.getString(6));
            smsInfo.setSendDate(cursor.getString(7));
            smsInfo.setSendTime(cursor.getString(8));
            smsInfo.setHidePhone(cursor.getString(9));
            smsInfo.setFileName(cursor.getString(10));
            smsInfo.setFileSize(cursor.getString(11));
            smsInfo.setFilePath(cursor.getString(12));
            smsInfo.setIsSuccess(cursor.getInt(13));
            smsInfo.setFileType(cursor.getString(14));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.d("smsDatabase---queryWhereIsRead",sql);
        return smsInfo;
    }

    /**
     * 重置
     * 
     * @param datas
     */
    public void reset(List<SmsInfo> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DatabaseHelper.SMS_TABLE_NAME);
            // 重新添加
            for (SmsInfo data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    public void destroy() {
        dbHelper.close();
    }
}