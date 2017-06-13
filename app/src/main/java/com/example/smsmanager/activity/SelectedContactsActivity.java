package com.example.smsmanager.activity;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.MessageEvent;
import com.example.smsmanager.db.ContactsDatabase;
import com.example.smsmanager.db.NewContactsDatabase;
import com.example.smsmanager.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/3/1.
 * 选择联系人页面
 */

public class SelectedContactsActivity extends AppCompatActivity implements View.OnClickListener, ContactListAdapter.CheckInterface, EasyPermissions.PermissionCallbacks {
    String TAG = "SelectedContactsActivity";
    ImageView iv_back;
    TextView tv_contacts_count;
    RecyclerView rv_contacts_list;
    CheckBox cb_selected_all;
    TextView tv_selected_count;//选中数量
    ImageView tv_ok;
    EditText et_search;
    ImageView iv_clear_search;
    TextView tv_no_data;
    Button btn_new_contacts;
    View ll_progress;

    private List<ContactsInfo> contactsInfoList = new ArrayList<>();
    Map<String, String> contactsInfoMap = new HashMap<>();
    ContactListAdapter adapter;
    ContactsDatabase contactsDatabase;
    NewContactsDatabase newContactsDatabase;

    private static final int READ_CONTACTS = 123;//请求手机信息权限
    private static final int RC_SETTINGS_SCREEN = 125;//请求码

    //上拉加载更多
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    int pageSize = 20;//每页几条
    int pageNum = 0;//第几页
    int pageCount;//总页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contacts);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_contacts_count = (TextView) findViewById(R.id.tv_contacts_count);
        rv_contacts_list = (RecyclerView) findViewById(R.id.rv_contacts_list);
        cb_selected_all = (CheckBox) findViewById(R.id.cb_selected_all);
        tv_selected_count = (TextView) findViewById(R.id.tv_selected_count);
        tv_ok = (ImageView) findViewById(R.id.tv_ok);
        et_search = (EditText) findViewById(R.id.et_search);
        iv_clear_search = (ImageView) findViewById(R.id.iv_clear_search);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        btn_new_contacts = (Button) findViewById(R.id.btn_new_contacts);
        ll_progress = findViewById(R.id.ll_progress);
        // 实例化联系人数据库
        contactsDatabase = new ContactsDatabase(this);
        newContactsDatabase = new NewContactsDatabase(this);
        requestPhonePermission();//请求手机联系人权限);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchKey = charSequence.toString();
                if (StringUtils.isEmpty(searchKey)) {
                    contactsInfoList.clear();
                    initContacts(0, false);
                    iv_clear_search.setVisibility(View.GONE);
                    long tcontactsCount = contactsDatabase.queryCount(" where sendPhone=" + AppConfig.SENDPHONE);
                    tv_contacts_count.setText("全部联系人 " + tcontactsCount + "");
                } else {
                    searchContacts(SelectedContactsActivity.this, searchKey);
                    iv_clear_search.setVisibility(View.VISIBLE);
                    tv_contacts_count.setText("找到 " + contactsInfoList.size() + " 个联系人");
                }
                iv_clear_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        et_search.setText("");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        cb_selected_all.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        btn_new_contacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setResult(400);
                finish();
                break;
            case R.id.cb_selected_all:
                if (contactsInfoList.size() > 0) {
                    doCheckAll();
                } else {
                    cb_selected_all.setChecked(false);
                    Toast.makeText(SelectedContactsActivity.this, "未找到匹配选项", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_ok:
                List<ContactsInfo> checkedList = new ArrayList<>();//选中的选项
                for (ContactsInfo contactsInfo : contactsInfoList) {
                    if (contactsInfo.isChoosed()) {
                        checkedList.add(contactsInfo);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("checkedList", (Serializable) checkedList);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(200, intent);
                finish();
                break;
            case R.id.btn_new_contacts:///新建联系人
                startActivityForResult(new Intent(SelectedContactsActivity.this, NewContactsActivity.class), 0);
                break;

        }
    }

    public void initContacts(int pageNum, boolean isLoadMore) {
        List<ContactsInfo> queryContactsList = new ArrayList<>();
        queryContactsList=contactsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE + " order by cid desc  limit " +  pageSize + " offset " + pageSize * pageNum);
        contactsInfoList.addAll(queryContactsList);
        if (!isLoadMore) {
            if (contactsInfoList.size() > 0) {
                rv_contacts_list.setVisibility(View.VISIBLE);
                tv_no_data.setVisibility(View.GONE);
            } else {
                rv_contacts_list.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
            }
            //Collections.reverse(contactsInfoList); // 倒序排列
            setAdapter(contactsInfoList);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    //加载适配器
    private void setAdapter(List<ContactsInfo> list) {
        long tcontactsCount = contactsDatabase.queryCount(" where sendPhone=" + AppConfig.SENDPHONE);
        tv_contacts_count.setText("全部联系人 " + tcontactsCount + "");
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new ContactListAdapter(this, list, "selectedContacts");
        rv_contacts_list.setAdapter(adapter);
        rv_contacts_list.setLayoutManager(linearLayoutManager);
        adapter.setCheckInterface(this);
        loadMore();//上拉加载更多
    }

    //checkedbox控件点击接口事件
    @Override
    public void checkChild(int position, boolean isChecked) {
        ContactsInfo contactsInfo = contactsInfoList.get(position);
        contactsInfo.setChoosed(isChecked);
        if (isAllCheck()) {
            cb_selected_all.setChecked(true);
        } else {
            cb_selected_all.setChecked(false);
        }
        calculate();
        adapter.notifyDataSetChanged();
    }

    //判断是否全部选中
    private boolean isAllCheck() {
        for (ContactsInfo contactsInfo : contactsInfoList) {
            if (!contactsInfo.isChoosed())
                return false;
        }
        return true;
    }

    /**
     * 全选与反选
     */
    private void doCheckAll() {
        for (int i = 0; i < contactsInfoList.size(); i++) {
            contactsInfoList.get(i).setChoosed(cb_selected_all.isChecked());
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
        for (int i = 0; i < contactsInfoList.size(); i++) {
            ContactsInfo cartInfo = contactsInfoList.get(i);
            if (cartInfo.isChoosed()) {
                selectedCount++;
            }
        }
        tv_selected_count.setText("(" + selectedCount + ")");
    }

    //根据联系人搜索联系人信息
    public void searchContacts(Context context, String searchKey) {
        contactsInfoList.clear();
        contactsInfoMap.clear();
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + searchKey);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name", "data1"}, null, null, null);
        cursor.moveToFirst(); // 游标移动到第一项
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String name = cursor.getString(0);
            String number = cursor.getString(1);
            Log.d(searchKey, number + ":" + name);
            contactsInfoMap.put(name, number);

        }
        for (Map.Entry<String, String> entry : contactsInfoMap.entrySet()) {
            ContactsInfo contactsInfo = new ContactsInfo();
            contactsInfo.setSendPhone(AppConfig.SENDPHONE);
            contactsInfo.setName(entry.getKey());
            contactsInfo.setPhone(entry.getValue());
            //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            contactsInfoList.add(contactsInfo);
        }
        if (contactsInfoList.size() > 0) {
            Collections.reverse(contactsInfoList); // 倒序排列
            rv_contacts_list.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
        } else {
            rv_contacts_list.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    //上拉加载类
    public void loadMore() {
        rv_contacts_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == contactsInfoList.size() - 1) {
                    pageNum++;
                    //模拟网络请求
                    if (pageNum > pageCount - 1) {
                        //模拟共有pageCount页数据
                        Toast.makeText(SelectedContactsActivity.this, "没有更多数据!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        initContacts(pageNum, true);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            switch (requestCode) {
                case 0:
                    contactsInfoList.clear();
                    initContacts(0, false);
                    long tcontactsCount = contactsDatabase.queryCount(" where sendPhone=" + AppConfig.SENDPHONE);
                    tv_contacts_count.setText("全部联系人 " + tcontactsCount + "");
                    break;
            }
        }
    }

    private class InsertContactsAsyncTask extends AsyncTask<Void, Void, Void> {
        String[] projection;
        Cursor cursor = null;
        Context context;

        public InsertContactsAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_progress.setVisibility(View.VISIBLE);
            contactsInfoMap.clear();
            contactsDatabase.delete("");
            // 查询的字段
            projection = new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.DATA1};
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				              projection, null, null, null);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    contactsInfoMap.put(name, number);
                }
                for (Map.Entry<String, String> entry : contactsInfoMap.entrySet()) {
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactsInfo.setSendPhone(AppConfig.SENDPHONE);
                    contactsInfo.setName(entry.getKey());
                    contactsInfo.setPhone(entry.getValue());
                    //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                    contactsDatabase.insert(contactsInfo);
                }
            } catch (Exception e) {
                Log.e("SekectedContactsActivity", "error:", e);
            } finally {
                cursor.close();
            }
            cursor.close();

            List<ContactsInfo> addContactsInfoList = newContactsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE);
            if (addContactsInfoList.size() > 0) {
                for (ContactsInfo contactsInfo : addContactsInfoList) {
                    contactsDatabase.insert(contactsInfo);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("SelectedContactsActivity", "完成");
            ll_progress.setVisibility(View.GONE);
            pageCount= contactsDatabase.queryCount(" where sendPhone=" + AppConfig.SENDPHONE)/pageSize+1;//计算总页数
            initContacts(0, false);
        }


    }

    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(300);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 请求手机联系人权限
     */
    @AfterPermissionGranted(READ_CONTACTS)
    public void requestPhonePermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            //异步任务添加联系人数据库
            InsertContactsAsyncTask insertContactsAsyncTask = new InsertContactsAsyncTask(this);
            insertContactsAsyncTask.execute();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.read_contacts_permission),
                    READ_CONTACTS, Manifest.permission.READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        //异步任务添加联系人数据库
        InsertContactsAsyncTask insertContactsAsyncTask = new InsertContactsAsyncTask(this);
        insertContactsAsyncTask.execute();
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
