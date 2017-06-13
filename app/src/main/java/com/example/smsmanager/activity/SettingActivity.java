package com.example.smsmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsmanager.AppConfig;
import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.MessageEvent;
import com.example.smsmanager.utils.DialogHelp;
import com.example.smsmanager.utils.StringUtils;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/3/6.
 * 设置页面
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    TextView tv_phone;
    Button tv_out;
    View rl_phone;
    View rl_about;
    View rl_emport;
    TextView tv_emport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_out= (Button) findViewById(R.id.tv_out);
        rl_emport= findViewById(R.id.rl_emport);
        rl_phone = findViewById(R.id.rl_phone);
        rl_about= findViewById(R.id.rl_about);
        tv_emport = (TextView) findViewById(R.id.tv_emport);
        tv_emport.setText(AppConfig.DEFAULT_SAVE_FILE_PATH);
        Intent intent=getIntent();
        String phone=intent.getStringExtra("phone");
        if(!StringUtils.isEmpty(phone)){
            tv_phone.setText(phone);
        }else{
            tv_phone.setText("设置手机号");
        }

    }

    public void initData() {
        iv_back.setOnClickListener(this);
        rl_phone.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        tv_out.setOnClickListener(this);
        rl_emport.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_phone:
                 startActivityForResult(new Intent(this,SettingPhoneActivity.class),0);
                break;
            case R.id.rl_about:
                startActivity(new Intent(this,SettingAboutActivity.class));
                break;

            case R.id.tv_out:
               AppManager.getAppManager().AppExit(this);
                break;
            case R.id.rl_emport:
                String message="当前导出文件存储位置是"+AppConfig.DEFAULT_SAVE_FILE_PATH+
                        ",是否要更换存储位置？";
                DialogHelp.getConfirmDialog(SettingActivity.this,message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent(SettingActivity.this,FileListActivity.class);
                        intent.putExtra("type","setting");
                        SettingActivity.this.startActivityForResult(intent,3);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            switch (requestCode) {
                case 0:
                    String setPhone=data.getStringExtra("setPhone");
                    if(!StringUtils.isEmpty(setPhone)){
                        tv_phone.setText(setPhone);
                    }
                    EventBus.getDefault().post(new MessageEvent(setPhone));
                    break;
                case 3:
                    String currentFilePath=data.getStringExtra("currentFilePath");
                    if(!StringUtils.isEmpty(currentFilePath)){
                        tv_emport.setText(currentFilePath);
                        AppConfig.DEFAULT_SAVE_FILE_PATH=currentFilePath;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
