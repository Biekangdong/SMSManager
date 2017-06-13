package com.example.smsmanager.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easypermissions.AfterPermissionGranted;
import com.example.easypermissions.AppSettingsDialog;
import com.example.easypermissions.EasyPermissions;
import com.example.smsmanager.AppConfig;
import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.adapter.ContactListAdapter;
import com.example.smsmanager.adapter.SMSListAdapter;
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.MessageEvent;
import com.example.smsmanager.bean.SmsInfo;
import com.example.smsmanager.db.ContactsDatabase;
import com.example.smsmanager.db.NewContactsDatabase;
import com.example.smsmanager.db.SmsDatabase;
import com.example.smsmanager.utils.DialogHelp;
import com.example.smsmanager.utils.LogcatHelper;
import com.example.smsmanager.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by dong on 2017/3/10.
 * 主页面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SMSListAdapter.OnItemClickLitener, SMSListAdapter.CheckInterface,EasyPermissions.PermissionCallbacks {
    String TAG="MainActivity";
    ImageView iv_back;
    TextView tv_title;
    TextView tv_setting;
    RecyclerView rc_sms_list;
    View ll_title_checked_no;

    View ll_title_checked;
    ImageView iv_close;
    Button btn_new_sms;
    CheckBox cb_selected_all;
    TextView tv_selected_count;

    SmsDatabase smsDatabase;
    List<String> receivePhoneList;
    SMSListAdapter adapter;
    List<SmsInfo> smsInfoList;
    boolean isLongClick = false;
    boolean isSendPhone = true;//判断是否发送方是否为空
    List<String> removePhoneList;
    ContactsDatabase contactsDatabase;
    private static final int READ_PHONE_STATE = 123;//请求手机信息权限
    private static final int RC_SETTINGS_SCREEN = 125;//请求码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(this);
        LogcatHelper.getInstance(this).start();//Log信息文件输出
        EventBus.getDefault().register(this);
        requestPhonePermission();//请求获取手机信息权限
        initView();
        initData();
        initTitle();
    }
    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        rc_sms_list = (RecyclerView) findViewById(R.id.rc_sms_list);
        ll_title_checked_no = findViewById(R.id.ll_title_checked_no);
        ll_title_checked = findViewById(R.id.ll_title_checked);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        btn_new_sms = (Button) findViewById(R.id.btn_new_sms);
        cb_selected_all = (CheckBox) findViewById(R.id.cb_selected_all);
        tv_selected_count = (TextView) findViewById(R.id.tv_selected_count);
        iv_back.setVisibility(View.GONE);
        smsDatabase = new SmsDatabase(this);
        contactsDatabase=new ContactsDatabase(this);
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        btn_new_sms.setOnClickListener(this);
        cb_selected_all.setOnClickListener(this);
    }

    public void isRead() {//判断是否已读
        Map<Integer, String> idReadMap = new HashMap<>();
        if (receivePhoneList.size() > 0) {
            for (int i = 0; i < receivePhoneList.size(); i++) {
                String s = receivePhoneList.get(i);
                List<SmsInfo> smsInfos = smsDatabase.queryWhere(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone=" +"'"+s+"'"
                        + " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone="+"'"+s+"'", " where receivePhone=" + AppConfig.SENDPHONE);
                if (smsInfos.size() > 0) {
                    for (SmsInfo smsInfo : smsInfos) {
                        if (smsInfo.getIsRead().equals("否")) {
                            idReadMap.put(i, "否");
                        } else {
                            idReadMap.put(i, "是");
                        }
                    }
                }
            }
        }
        for (Map.Entry<Integer, String> entry : idReadMap.entrySet()) {
            //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            smsInfoList.get(entry.getKey()).setIsRead(entry.getValue());
        }
        adapter.notifyDataSetChanged();
    }

    public void initTitle() {
        tv_title.setText("信息");
    }

    //设置手机号
    public void initSetPhone(){
        //判断是否安装sim卡
        AppConfig.SENDPHONE = StringUtils.getLocalPhone(this);
        if (StringUtils.isEmpty(AppConfig.SENDPHONE)) {
            String currentPhone = AppConfig.getAppConfig(this).get("currentPhone");
            AppConfig.SENDPHONE = currentPhone;
            if (StringUtils.isEmpty(AppConfig.SENDPHONE)) {
                String message = "你还未设置手机号码，请在设置里面设置手机号";
                DialogHelp.getConfirmDialog(MainActivity.this, message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                intent.putExtra("phone", AppConfig.SENDPHONE);
                                startActivity(intent);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        }
    }
    //初始List化列表
    public void initList() {
        if (StringUtils.isEmpty(AppConfig.SENDPHONE)) {
            smsInfoList = new ArrayList<>();
        } else {
            smsInfoList = smsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE + " and hidePhone!="+AppConfig.SENDPHONE+" group by receivePhone");
            String sendPhone = AppConfig.getAppConfig(this).get("sendPhone");
            //当前查询为空时，显示上个相关信息
            if ((!StringUtils.isEmpty(sendPhone)) && smsInfoList.size() <= 0) {
                smsInfoList = smsDatabase.query(" where sendPhone=" + sendPhone + " and receivePhone="+AppConfig.SENDPHONE+" and hidePhone!="+AppConfig.SENDPHONE+" group by receivePhone");
                isSendPhone = false;
                ll_title_checked_no.setVisibility(View.VISIBLE);
                ll_title_checked.setVisibility(View.GONE);
                btn_new_sms.setText("新建");
            } else {
                isSendPhone = true;
            }
        }
        receivePhoneList = new ArrayList<>();
        for (SmsInfo smsInfo : smsInfoList) {
            receivePhoneList.add(smsInfo.getReceivePhone());
        }
        adapter = new SMSListAdapter(this, smsInfoList, isLongClick, isSendPhone);
        rc_sms_list.setAdapter(adapter);
        rc_sms_list.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickLitener(this);
        adapter.setCheckInterface(this);
        isRead();//判断是否已读

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("phone", AppConfig.SENDPHONE);
                startActivity(intent);
                break;
            case R.id.iv_close:
                isLongClick = false;
                adapter.setLongClick(isLongClick);
                adapter.notifyDataSetChanged();
                ll_title_checked_no.setVisibility(View.VISIBLE);
                ll_title_checked.setVisibility(View.GONE);
                btn_new_sms.setText("新建");
                break;
            case R.id.btn_new_sms:
                if (btn_new_sms.getText().equals("新建")) {
                    startActivity(new Intent(this, NewSMSActivity.class));
                } else {
                    removePhoneList = new ArrayList<>();
                    for (SmsInfo smsInfo : smsInfoList) {
                        if (smsInfo.isChoosed()) {
                            removePhoneList.add(smsInfo.getReceivePhone());
                        }
                    }
                    if (removePhoneList.size() <= 0) {
                        Toast.makeText(MainActivity.this, "请选择要删除的选项", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DialogHelp.getConfirmDialog(MainActivity.this, "确定要删除此信息吗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int j = 0; j < removePhoneList.size(); j++) {
                                        List<SmsInfo> removeSmsInfoList = smsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone="
                                                + "'"+removePhoneList.get(j) + "'"+ " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone=" + "'"+removePhoneList.get(j) + "'");
                                        for (SmsInfo smsInfo : removeSmsInfoList) {
                                            smsDatabase.delete(Integer.valueOf(smsInfo.getSid()));
                                        }
                                    }
                                    initList();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();
                }
                break;
            case R.id.cb_selected_all:
                if (smsInfoList.size() > 0) {
                    doCheckAll();
                } else {
                    cb_selected_all.setChecked(false);
                    Toast.makeText(MainActivity.this, "你还没有信息", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
  //列表单击事件
    @Override
    public void OnItemClick(View view, int position) {
        SmsInfo smsInfo = smsInfoList.get(position);
        Intent intent = new Intent(MainActivity.this, SMSDetailsActivity.class);
        if (isSendPhone) {
            intent.putExtra("receiveName", smsInfo.getReceiveName());
            intent.putExtra("receivePhone", smsInfo.getReceivePhone());
        } else {
            intent.putExtra("receiveName", smsInfo.getSendName());
            intent.putExtra("receivePhone", smsInfo.getSendPhone());
        }

        intent.putExtra("sendName", smsInfo.getSendName());
        intent.putExtra("sendPhone", smsInfo.getSendPhone());
        intent.putExtra("type", "itemSMS");
        startActivity(intent);
    }
    //列表长按事件
    @Override
    public void OnItemLongClick(View view, int position) {
        isLongClick = true;
        adapter.setLongClick(isLongClick);
        adapter.notifyDataSetChanged();
        ll_title_checked_no.setVisibility(View.GONE);
        ll_title_checked.setVisibility(View.VISIBLE);
        btn_new_sms.setText("删除");
    }
    //checkedbox点击事件
    @Override
    public void checkChild(int position, boolean isChecked) {
        SmsInfo smsInfo = smsInfoList.get(position);
        smsInfo.setChoosed(isChecked);
        if (isAllCheck()) {
            cb_selected_all.setChecked(true);
        } else {
            cb_selected_all.setChecked(false);
        }
        calculate();
        adapter.notifyDataSetChanged();
    }
  //判断全选
    private boolean isAllCheck() {
        for (SmsInfo smsInfo : smsInfoList) {
            if (!smsInfo.isChoosed())
                return false;
        }
        return true;
    }

    /**
     * 全选与反选
     */
    private void doCheckAll() {
        for (int i = 0; i < smsInfoList.size(); i++) {
            smsInfoList.get(i).setChoosed(cb_selected_all.isChecked());
        }
        calculate();
        adapter.notifyDataSetChanged();
    }

    /**
     * 统计操作<br>
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     * 3.给底部的textView进行数据填充
     */
    private void calculate() {
        int selectedCount = 0;
        //总计所有价格
        for (int i = 0; i < smsInfoList.size(); i++) {
            SmsInfo smsInfo = smsInfoList.get(i);
            if (smsInfo.isChoosed()) {
                selectedCount++;
            }
        }
        tv_selected_count.setText("(" + selectedCount + ")");
    }

    @Subscribe
    public void onEventMainThread(MessageEvent<String> event) {
        String msg = event.getMsg();
        if (!StringUtils.isEmpty(msg)) {
            AppConfig.SENDPHONE = msg;
            AppConfig.getAppConfig(this).set("currentPhone", msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LogcatHelper.getInstance(this).stop();
    }

    //记录用户首次点击返回键的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByDoubleClick();
        }
        return false;
    }

    //双击退出
    boolean isExit = false;

    private void exitByDoubleClick() {
        Timer timer = null;
        if (!isExit) {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            LogcatHelper.getInstance(this).stop();
            finish();
            System.exit(0);
        }
    }

    /**
     * 请求手机信息权限
     */
    @AfterPermissionGranted(READ_PHONE_STATE)
    public void requestPhonePermission() {
        if (EasyPermissions.hasPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE})) {
            initSetPhone();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.phone_permission),
                    READ_PHONE_STATE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE});
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    //成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        initSetPhone();
    }
    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }
}
