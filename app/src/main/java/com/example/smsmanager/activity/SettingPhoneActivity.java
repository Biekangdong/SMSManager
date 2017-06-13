package com.example.smsmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.utils.StringUtils;

/**
 * Created by admin on 2017/3/6.
 * 设置手机号页面
 */

public class SettingPhoneActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView iv_back;
    EditText et_phone;
    Button btn_sava;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_phone);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_phone= (EditText) findViewById(R.id.et_phone);
        btn_sava= (Button) findViewById(R.id.btn_sava);
    }
    public void initData() {
        iv_back.setOnClickListener(this);
        btn_sava.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_sava:
                String setPhone=et_phone.getText().toString();
                if(StringUtils.isEmpty(setPhone)){
                    Toast.makeText(SettingPhoneActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent();
                intent.putExtra("setPhone",setPhone);
                setResult(200,intent);
                finish();
                break;

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
