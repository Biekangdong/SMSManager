package com.example.smsmanager.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smsmanager.bean.ContactsInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by admin on 2017/3/2.
 * 新建联系人数据库操作类
 */

public class NewContactsDatabase {
    private final DatabaseHelper dbHelper;

    public NewContactsDatabase(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * 增
     * 
     * @param data
     */
    public void insert(ContactsInfo data) {
        String sql = "insert into " + DatabaseHelper.NEWCONTACTS_TABLE_NAME;

        sql += "(sendPhone, name, phone) values(?,?,?)";

        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL(sql, new String[] {data.getSendPhone() + "",data.getName() + "", data.getPhone() + ""});
        sqlite.close();
    }

    /**
     * 删
     * 
     * @param where
     */
    public void delete(String where) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = "delete from " + DatabaseHelper.NEWCONTACTS_TABLE_NAME + where;
        sqlite.execSQL(sql);
        sqlite.close();
    }

    /**
     * 改
     * 
     * @param data
     */
    public void update(ContactsInfo data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("update " + DatabaseHelper.NEWCONTACTS_TABLE_NAME + " set sendPhone=?,name=?,phone=? where cid=?");
        sqlite.execSQL(sql,
                new String[] { data.getSendPhone() + "", data.getName() + "", data.getPhone()+ "", data.getCid()+ ""});
        sqlite.close();
    }
    /**
     * 查一条数据
     *
     * @param where
     * @return
     */
    public ContactsInfo queryContactsInfo(String where) {
        ContactsInfo contactsInfo = null;
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
       String sql= "select * from "
                + DatabaseHelper.NEWCONTACTS_TABLE_NAME + where;
        Cursor cursor = sqlite.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            contactsInfo= new ContactsInfo();
            contactsInfo.setCid(cursor.getInt(0));
            contactsInfo.setSendPhone(cursor.getString(1));
            contactsInfo.setName(cursor.getString(2));
            contactsInfo.setPhone(cursor.getString(3));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.d("ContactsDatabase---queryContactsInfo",sql);
        return contactsInfo;
    }

    /**
     * 查
     * 
     * @param where
     * @return
     */
    public List<ContactsInfo> query(String where) {
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();
        ArrayList<ContactsInfo> data = null;
        data = new ArrayList<ContactsInfo>();
        String sql="select * from "
                + DatabaseHelper.NEWCONTACTS_TABLE_NAME + where;
        Cursor cursor = sqlite.rawQuery(sql, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ContactsInfo contactsInfo = new ContactsInfo();
            contactsInfo.setSendPhone(cursor.getString(1));
            contactsInfo.setName(cursor.getString(2));
            contactsInfo.setPhone(cursor.getString(3));
            data.add(contactsInfo);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        sqlite.close();
        Log.d("ContactsDatabase---query",sql);
        return data;
    }

    /**
     * 重置
     * 
     * @param datas
     */
    public void reset(List<ContactsInfo> datas) {
        if (datas != null) {
            SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
            // 删除全部
            sqlite.execSQL("delete from " + DatabaseHelper.NEWCONTACTS_TABLE_NAME);
            // 重新添加
            for (ContactsInfo data : datas) {
                insert(data);
            }
            sqlite.close();
        }
    }

    public void destroy() {
        dbHelper.close();
    }
}