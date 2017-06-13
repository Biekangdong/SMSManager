package com.example.smsmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2017/3/2.
 * 创建数据库，以及表帮助类
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SMSManager.db";

    public static final String NEWCONTACTS_TABLE_NAME = "new_contacts_table";
    public static final String CONTACTS_TABLE_NAME = "contacts_table";
    public static final String SMS_TABLE_NAME = "sms_table";


    public static final String CREATE_NEW_CONTACTS_TABLE = "create table "
            +NEWCONTACTS_TABLE_NAME
            + " (cid integer primary key autoincrement,sendPhone varchar(50),name varchar(50) not null,phone varchar(50) not null)";
    public static final String CREATE_CONTACTS_TABLE = "create table "
            +CONTACTS_TABLE_NAME
            + " (cid integer primary key autoincrement,sendPhone varchar(50),name varchar(50) not null,phone varchar(50) not null)";
    public static final String CREATE_SMS_TABLE = "create table "
            +SMS_TABLE_NAME
            + " (sid integer primary key autoincrement,sendName varchar(50) not null,sendPhone varchar(50) not null,"
            +"receiveName varchar(50) not null,receivePhone varchar(50) not null,"
            +"smsContent varchar(200),isRead varchar(10),sendDate varchar(100),sendTime varchar(100),hidePhone varchar(50),"
            +"fileName varchar(50),fileSize varchar(10),filePath varchar(50),isSuccess integer,fileType varchar(50))";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEW_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_SMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}