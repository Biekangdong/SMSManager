package com.example.smsmanager.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smsmanager.AppConfig;
import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.adapter.ContactListAdapter;
import com.example.smsmanager.adapter.FileInputAdapter;
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.SmsInfo;
import com.example.smsmanager.db.ContactsDatabase;
import com.example.smsmanager.db.SmsDatabase;
import com.example.smsmanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/28.
 * 新建短信页面
 */

public class NewSMSActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    ImageView tv_right_menu;
    EditText et_search;
    RecyclerView rv_contacts_list;
    EditText et_input;
    ImageView iv_send;
    RecyclerView rcy_input_list;
    ImageView iv_file_open;

    StringBuffer contactsString = new StringBuffer();
    ContactsDatabase contactsDatabase;
    SmsDatabase smsDatabase;
    boolean isOpenInputFile=true;
    String receiveName,receivePhone;
    String smsContent;

    private int lastVisibleItemPosition = 0;// 标记上次滑动位置，初始化默认为0
    private boolean scrollFlag = false;// 标记是否滑动
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sms);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
        initInputRecycleView();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_right_menu = (ImageView) findViewById(R.id.tv_right_menu);
        et_search = (EditText) findViewById(R.id.et_search);
        rv_contacts_list = (RecyclerView) findViewById(R.id.rv_contacts_list);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        et_input = (EditText) findViewById(R.id.et_input);
        rcy_input_list= (RecyclerView) findViewById(R.id.rcy_input_list);
        iv_file_open = (ImageView) findViewById(R.id.iv_file_open);
        // 实例化联系人数据库
        contactsDatabase = new ContactsDatabase(this);
        smsDatabase = new SmsDatabase(this);
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        tv_right_menu.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        iv_file_open.setOnClickListener(this);
        et_input.setOnClickListener(this);
        initList();
        KeyBoard();
    }
    //监听软键盘状态
    public void KeyBoard() {
        rv_contacts_list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                        ||scrollState ==AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollFlag) {
                    if (dy>0) {
                        Log.d("dc", "上滑");
                        //TODO
                    } else if (dy<0) {
                        // 下滑
                        Log.d("dc", "下滑");
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                                (NewSMSActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
            case R.id.et_input://输入框点击
                if(!isOpenInputFile){
                    rcy_input_list.setVisibility(View.GONE);
                    isOpenInputFile=true;
                }
                break;
            case R.id.tv_right_menu://获取联系人
                startActivityForResult(new Intent(this, SelectedContactsActivity.class), 0);
                break;
            case R.id.iv_file_open://打开文件上传界面
                  if(isOpenInputFile){
                      rcy_input_list.setVisibility(View.VISIBLE);
                      isOpenInputFile=false;
                      ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                              (NewSMSActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                  }else{
                      rcy_input_list.setVisibility(View.GONE);
                      isOpenInputFile=true;
                  }
                break;
            case R.id.iv_send://发送消息
                String contactsStr = contactsString.toString() + ",";
                if (StringUtils.isEmpty(contactsStr)) {
                    Toast.makeText(NewSMSActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                smsContent = et_input.getText().toString();
                if (StringUtils.isEmpty(smsContent)) {
                    Toast.makeText(NewSMSActivity.this, "信息内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(AppConfig.SENDPHONE)) {
                    Toast.makeText(NewSMSActivity.this, "请先设置手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendData();
                break;

        }
    }
    //发送短信
    public void sendData(){
        StringBuffer stringBuffersendName = new StringBuffer();
        StringBuffer stringBuffersendPhone = new StringBuffer();
        String receivePhoneStr=et_search.getText().toString()+",";
        String[] contacts = receivePhoneStr.split(",");
        String sendPhone = AppConfig.SENDPHONE;
        String sendName;
        ContactsInfo contactsInfoSend = contactsDatabase.queryContactsInfo(" where phone=" + sendPhone);
        if (contactsInfoSend != null) {
            sendName = contactsInfoSend.getName();
        } else {
            sendName = sendPhone;
        }
        if (contacts.length > 0) {
            for (int i=0;i<contacts.length;i++) {
                ContactsInfo contactsInfo = contactsDatabase.queryContactsInfo(" where phone=" +"'"+ contacts[i]+"'");
                if(contactsInfo!=null){
                    receiveName = contactsInfo.getName();
                    receivePhone = contactsInfo.getPhone();
                }else{
                    receiveName = contacts[i];
                    receivePhone =contacts[i];
                }


                if(contacts.length>1) {//只有一个时，不用一个个添加
                    SmsInfo smsInfo = new SmsInfo();
                    smsInfo.setSendName(sendName);
                    smsInfo.setSendPhone(sendPhone);
                    smsInfo.setReceiveName(receiveName);
                    smsInfo.setReceivePhone(receivePhone);
                    String sendDateStr = StringUtils.getCurrentTimeString();
                    smsInfo.setSendDate(sendDateStr);
                    smsInfo.setSmsContent(smsContent);
                    smsInfo.setFileName("");
                    smsInfo.setIsRead("否");
                    String sendDateDateStr = StringUtils.getCurrentDateDateString();
                    smsInfo.setSendDate(sendDateDateStr);
                    String sendDateTimeStr = StringUtils.getCurrentDateTimeString();
                    smsInfo.setSendTime(sendDateTimeStr);
                    smsInfo.setHidePhone(sendPhone);
                    smsDatabase.insert(smsInfo);
                }
                if(i==contacts.length-1){
                    stringBuffersendName.append(receiveName);
                    stringBuffersendPhone.append(receivePhone );
                }else {
                    stringBuffersendName.append(receiveName + ",");
                    stringBuffersendPhone.append(receivePhone + ",");
                }

            }

            SmsInfo smsInfo = new SmsInfo();
            smsInfo.setSendName(sendName);
            smsInfo.setSendPhone(sendPhone);
            smsInfo.setReceiveName(stringBuffersendName.toString());
            smsInfo.setReceivePhone(stringBuffersendPhone.toString());
            String sendDateStr = StringUtils.getCurrentTimeString();
            smsInfo.setSendDate(sendDateStr);
            smsInfo.setSmsContent(smsContent);
            smsInfo.setFileName("");
            smsInfo.setIsRead("否");
            String sendDateDateStr = StringUtils.getCurrentDateDateString();
            smsInfo.setSendDate(sendDateDateStr);
            String sendDateTimeStr = StringUtils.getCurrentDateTimeString();
            smsInfo.setSendTime(sendDateTimeStr);
            smsDatabase.insert(smsInfo);
            Intent intent = new Intent(this, SMSDetailsActivity.class);
            intent.putExtra("receiveName", stringBuffersendName.toString());
            intent.putExtra("receivePhone", stringBuffersendPhone.toString());
            intent.putExtra("type", "newSMS");
            startActivity(intent);
            AppConfig.getAppConfig(this).set("sendPhone",AppConfig.SENDPHONE);
            //判断是否已有该联系人，添加联系人
            List<ContactsInfo> addContactsInfoList = contactsDatabase.query(" where phone=" + AppConfig.SENDPHONE);
            if(addContactsInfoList.size()<=0){
                ContactsInfo contactsInfo = new ContactsInfo();
                contactsInfo.setSendPhone("");
                contactsInfo.setName(AppConfig.SENDPHONE);
                contactsInfo.setPhone(AppConfig.SENDPHONE);
                contactsDatabase.insert(contactsInfo);
            }
            finish();
        }
    }
    //初始化list
    public void initList() {
        final List<ContactsInfo> contactsInfoList = new ArrayList<>();
        List<SmsInfo> smsInfoList;
        if(StringUtils.isEmpty(AppConfig.SENDPHONE)){
            smsInfoList=new ArrayList<>();
        }else {
            smsInfoList= smsDatabase.query(" where sendPhone="+AppConfig.SENDPHONE +" group by receivePhone");
        }

        if (smsInfoList.size() > 0) {
            for (SmsInfo smsInfo : smsInfoList) {
                ContactsInfo contactsInfo = new ContactsInfo();
                contactsInfo.setName(smsInfo.getReceiveName());
                contactsInfo.setPhone(smsInfo.getReceivePhone());
                contactsInfoList.add(contactsInfo);
            }
        }

        if (contactsInfoList.size() > 0) {
            ContactListAdapter adapter = new ContactListAdapter(this, contactsInfoList, "newSMS");
            rv_contacts_list.setAdapter(adapter);
            rv_contacts_list.setLayoutManager(new LinearLayoutManager(this));
            adapter.setOnItemClickLitener(new ContactListAdapter.OnItemClickLitener() {
                @Override
                public void OnItemClick(View view, int position) {
                    ContactsInfo contactsInfo=contactsInfoList.get(position);
                    contactsString=new StringBuffer();
                    contactsString.append(contactsInfo.getPhone());
                    et_search.setText(contactsString.toString());
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            Bundle bundle;
            switch (requestCode) {
                case 0:
                    bundle= data.getExtras();
                    List<ContactsInfo> checkedList = (List<ContactsInfo>) bundle.getSerializable("checkedList");
                    if (checkedList.size() > 0) {
                        for (int i = 0; i < checkedList.size(); i++) {
                            if (i == checkedList.size() - 1) {
                                contactsString.append(checkedList.get(i).getPhone());
                            } else {
                                contactsString.append(checkedList.get(i).getPhone() + ",");
                            }
                        }
                        et_search.setText(contactsString.toString());
                    }
                    break;
                case 1:
                    if (StringUtils.isEmpty(et_search.getText().toString())) {
                        Toast.makeText(NewSMSActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    bundle=data.getExtras();
                    StringBuffer stringBuffersendName = new StringBuffer();
                    StringBuffer stringBuffersendPhone = new StringBuffer();
                    String receivePhoneStr=et_search.getText().toString()+",";
                    String[] contacts = receivePhoneStr.split(",");
                    if (contacts.length > 0) {
                        for (int i = 0; i < contacts.length; i++) {
                            ContactsInfo contactsInfo = contactsDatabase.queryContactsInfo(" where phone=" + "'" + contacts[i] + "'");
                            if(contactsInfo!=null){
                                receiveName = contactsInfo.getName();
                                receivePhone = contactsInfo.getPhone();
                            }else{
                                receiveName=contacts[i];
                                receivePhone=contacts[i];
                            }


                            if (i == contacts.length - 1) {
                                stringBuffersendName.append(receiveName);
                                stringBuffersendPhone.append(receivePhone);
                            } else {
                                stringBuffersendName.append(receiveName + ",");
                                stringBuffersendPhone.append(receivePhone + ",");
                            }
                        }
                        Intent intent = new Intent(this, SMSDetailsActivity.class);
                        intent.putExtra("receiveName", stringBuffersendName.toString());
                        intent.putExtra("receivePhone", stringBuffersendPhone.toString());
                        intent.putExtra("type", "newSMS");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        AppConfig.getAppConfig(this).set("sendPhone",AppConfig.SENDPHONE);
                    }
                    break;
            }
        }else if(resultCode == 300){
            String selectedFilePath=data.getStringExtra("selectedFilePath");
            Intent intent = new Intent(this, SMSDetailsActivity.class);
            intent.putExtra("selectedFilePath",selectedFilePath);
            startActivity(intent);
        }else if(resultCode == 400){//清空联系人
            et_search.setText("");
            et_input.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }
    //底部文件选择框口
    public void initInputRecycleView(){
        final String[] titles={"文档","图片","音频","视频"};
        final int[] images={R.drawable.file_document,R.drawable.file_picture,R.drawable.file_music,R.drawable.file_video};
        FileInputAdapter fileInputAdapter=new FileInputAdapter(this,titles,images);
        rcy_input_list.setAdapter(fileInputAdapter);
        rcy_input_list.setLayoutManager(new GridLayoutManager(this,4));
        fileInputAdapter.setOnItemClickLitener(new FileInputAdapter.OnItemClickLitener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent=new Intent(NewSMSActivity.this,SelectedFilesActivity.class);
                intent.putExtra("type",titles[position]);
                startActivityForResult(intent,1);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!isOpenInputFile){
                rcy_input_list.setVisibility(View.GONE);
                isOpenInputFile=true;
                return true;
            }else{
                finish();
            }
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
