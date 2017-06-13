package com.example.smsmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;

/**
 * Created by dong on 2017/3/12.
 * 关于页面
 */

public class SettingAboutActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }
    public void initData() {
        iv_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
