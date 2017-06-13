package com.example.smsmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smsmanager.AppConfig;
import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.MessageEvent;
import com.example.smsmanager.db.ContactsDatabase;
import com.example.smsmanager.db.NewContactsDatabase;
import com.example.smsmanager.utils.StringUtils;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/3/9.
 * 新建联系人页面
 */

public class NewContactsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    ImageView tv_ok;
    EditText et_name;
    EditText et_phone;
    NewContactsDatabase newContactsDatabase;
    ContactsDatabase contactsDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contacts);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
        //newContactsDatabase.delete("");
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_ok = (ImageView) findViewById(R.id.tv_ok);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        newContactsDatabase = new NewContactsDatabase(this);
        contactsDatabase=new ContactsDatabase(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_ok:
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString();
                if (StringUtils.isEmpty(name)) {
                    Toast.makeText(NewContactsActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(phone)) {
                    Toast.makeText(NewContactsActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isAdd = true;
                List<ContactsInfo> addContactsInfoList = newContactsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE);
                for (ContactsInfo contactsInfo1 : addContactsInfoList) {
                    if (contactsInfo1.getPhone().equals(phone)) {
                        isAdd = false;
                    } else {
                        isAdd = true;
                    }
                }
                if (isAdd) {
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactsInfo.setSendPhone(AppConfig.SENDPHONE);
                    contactsInfo.setName(name);
                    contactsInfo.setPhone(phone);
                    newContactsDatabase.insert(contactsInfo);
                    contactsDatabase.insert(contactsInfo);
                    setResult(200);
                    finish();
                } else {
                    Toast.makeText(NewContactsActivity.this, "此联系人已存在", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
