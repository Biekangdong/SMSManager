package com.example.smsmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsmanager.AppConfig;
import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.adapter.FileInputAdapter;
import com.example.smsmanager.adapter.SMSDetailsExpandAdapter;
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.FileSelectedInfo;
import com.example.smsmanager.bean.SmsExpandInfo;
import com.example.smsmanager.bean.SmsInfo;
import com.example.smsmanager.db.ContactsDatabase;
import com.example.smsmanager.db.SmsDatabase;
import com.example.smsmanager.utils.DialogHelp;
import com.example.smsmanager.utils.FileUtils;
import com.example.smsmanager.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/2.
 * 短信详情页面
 */

public class SMSDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    ExpandableListView lv_sms_details;
    TextView tv_name, tv_phone;
    EditText et_input;
    ImageView iv_send;
    RecyclerView rcy_input_list;
    ImageView iv_file_open;
    TextView tv_progress;

    SMSDetailsExpandAdapter smsDetailsExpandAdapter;
    SmsDatabase smsDatabase;
    ContactsDatabase contactsDatabase;
    List<SmsInfo> smsInfoList;
    String receiveName, receivePhone;
    String smsContent;
    String type;
    boolean isOpenInputFile = true;
    String receivePhoneStr;
    List<FileSelectedInfo> fileSelectedList = new ArrayList<>();

    private int lastVisibleItemPosition = 0;// 标记上次滑动位置，初始化默认为0
    private boolean scrollFlag = false;// 标记是否滑动
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_details);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
        initIsRead();
        initInputRecycleView();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        lv_sms_details = (ExpandableListView) findViewById(R.id.lv_sms_details);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        et_input = (EditText) findViewById(R.id.et_input);
        rcy_input_list = (RecyclerView) findViewById(R.id.rcy_input_list);
        iv_file_open = (ImageView) findViewById(R.id.iv_file_open);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        smsDatabase = new SmsDatabase(this);
        contactsDatabase = new ContactsDatabase(this);

        Intent intent = getIntent();
        if (intent != null) {
            receiveName = intent.getStringExtra("receiveName");
            receivePhone = intent.getStringExtra("receivePhone");
            type = intent.getStringExtra("type");
            receivePhoneStr = receivePhone + ",";
            String[] strings = receivePhoneStr.split(",");
            tv_name.setText(receiveName);
            if (strings.length > 1) {
                tv_phone.setVisibility(View.GONE);
            } else {
                tv_phone.setVisibility(View.VISIBLE);
                tv_phone.setText(receivePhone);
            }
            Bundle bundle = intent.getExtras();//文件选择传传过来的值
            initImportFiles(bundle);

            String fileFolderPath = intent.getStringExtra("selectedFilePath");//文件夹文件传过来的值
            if (!StringUtils.isEmpty(fileFolderPath)) {
                initImportFolderFiles(fileFolderPath);
            }
        }
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        iv_file_open.setOnClickListener(this);
        et_input.setOnClickListener(this);
        initApapter();
        KeyBoard();
    }

    //接收者状态更新为已读
    public void initIsRead() {
        if (type.equals("newSMS")) {
            smsInfoList = smsDatabase.queryWhere(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone=" + "'" + receivePhone + "'"
                    + " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone=" + "'" + receivePhone + "'", " where sendPhone=" + AppConfig.SENDPHONE);
        } else if (type.equals("itemSMS")) {
            smsInfoList = smsDatabase.queryWhere(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone=" + "'" + receivePhone + "'"
                    + " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone=" + "'" + receivePhone + "'", " where receivePhone=" + AppConfig.SENDPHONE);
        }
        if (smsInfoList.size() > 0) {
            for (SmsInfo smsInfo : smsInfoList) {
                if (smsInfo.getIsRead().equals("否")) {
                    smsInfo.setIsRead("是");
                    smsDatabase.updateIsRead(smsInfo);
                }
            }
        }
    }

    //监听软键盘状态
    public void KeyBoard() {
        //ExpandableListView滑动方向监听
        lv_sms_details.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                        ||scrollState ==AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    scrollFlag = true;
                } else {
                    scrollFlag = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("dc", "firstVisibleItem：：" + firstVisibleItem + ":visibleItemCount:" + visibleItemCount + ":totalItemCount:" + totalItemCount);
                if (scrollFlag) {
                    if (firstVisibleItem < lastVisibleItemPosition) {
                        Log.d("dc", "上滑");
                        //TODO
                    } else if (firstVisibleItem > lastVisibleItemPosition) {
                        // 下滑
                        Log.d("dc", "下滑");
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                                (SMSDetailsActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    } else {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;//更新位置
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.et_input://输入框点击
                if (!isOpenInputFile) {
                    rcy_input_list.setVisibility(View.GONE);
                    isOpenInputFile = true;
                }
                break;
            case R.id.iv_file_open://打开文件上传界面
                if (isOpenInputFile) {
                    rcy_input_list.setVisibility(View.VISIBLE);
                    isOpenInputFile = false;
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                            (SMSDetailsActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    rcy_input_list.setVisibility(View.GONE);
                    isOpenInputFile = true;
                }
                break;
            case R.id.iv_send://发送消息
                if (StringUtils.isEmpty(receivePhoneStr)) {
                    Toast.makeText(SMSDetailsActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                smsContent = et_input.getText().toString();
                if (StringUtils.isEmpty(smsContent)) {
                    Toast.makeText(SMSDetailsActivity.this, "信息内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(AppConfig.SENDPHONE)) {
                    Toast.makeText(SMSDetailsActivity.this, "手机号码为空，请先设置手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendData(null);//发送短信
                break;
        }
    }

    //发送短信
    public void sendData(FileSelectedInfo fileInfo) {
        StringBuffer stringBuffersendName = new StringBuffer();
        StringBuffer stringBuffersendPhone = new StringBuffer();
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
            for (int i = 0; i < contacts.length; i++) {
                ContactsInfo contactsInfo = contactsDatabase.queryContactsInfo(" where phone=" + "'" + contacts[i] + "'");
                if (contactsInfo != null) {
                    receiveName = contactsInfo.getName();
                    receivePhone = contactsInfo.getPhone();
                } else {
                    receiveName = contacts[i];
                    receivePhone = contacts[i];
                }


                if (contacts.length > 1) {//只有一个时，设置要隐藏的phone
                    SmsInfo smsInfo = addSms(sendName, sendPhone, fileInfo);
                    smsInfo.setHidePhone(sendPhone);
                    smsDatabase.insert(smsInfo);
                }
                if (i == contacts.length - 1) {
                    stringBuffersendName.append(receiveName);
                    stringBuffersendPhone.append(receivePhone);
                } else {
                    stringBuffersendName.append(receiveName + ",");
                    stringBuffersendPhone.append(receivePhone + ",");
                }

            }

            SmsInfo smsInfo = addSms(sendName, sendPhone, fileInfo);
            ;
            smsDatabase.insert(smsInfo);
            //Toast.makeText(NewSMSActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
            AppConfig.getAppConfig(this).set("sendPhone", AppConfig.SENDPHONE);
            initApapter();//加载适配器
            et_input.setText("");
            initIsRead();//加载是否已读
        }
    }

    //添加短信信息
    public SmsInfo addSms(String sendName, String sendPhone, FileSelectedInfo fileInfo) {
        SmsInfo smsInfo = new SmsInfo();
        smsInfo.setSendName(sendName);
        smsInfo.setSendPhone(sendPhone);
        smsInfo.setReceiveName(receiveName);
        smsInfo.setReceivePhone(receivePhone);
        String sendDateStr = StringUtils.getCurrentTimeString();
        smsInfo.setSendDate(sendDateStr);
        if (fileInfo != null) {
            smsInfo.setFileName(fileInfo.getFileName());
            smsInfo.setFilePath(fileInfo.getFilePath());
            smsInfo.setFileSize(fileInfo.getFilesize());
            smsInfo.setIsSuccess(-1);
            smsInfo.setSmsContent(fileInfo.getFileName());
            String fileType = FileUtils.getMIMEType(fileInfo.getFilePath());
            String[] fileTypes = fileType.split("/");
            smsInfo.setFileType(fileTypes[0]);
        } else {
            smsInfo.setFileName("");
            smsInfo.setSmsContent(smsContent);
        }
        smsInfo.setIsRead("否");
        String sendDateDateStr = StringUtils.getCurrentDateDateString();
        smsInfo.setSendDate(sendDateDateStr);
        String sendDateTimeStr = StringUtils.getCurrentDateTimeString();
        smsInfo.setSendTime(sendDateTimeStr);
        return smsInfo;
    }

    //初始化适配器
    public void initApapter() {
        String sendPhone = AppConfig.getAppConfig(this).get("sendPhone");
        smsInfoList = smsDatabase.query(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone=" + "'" + receivePhone + "'"
                + " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone=" + "'" + receivePhone + "'"
                + " group by date(sendDate)");
        if (smsInfoList.size() <= 0) {
            smsInfoList = smsDatabase.query(" where sendPhone=" + "'" + sendPhone + "'" + " and receivePhone=" + "'" + receivePhone + "'"
                    + " or receivePhone=" + "'" + sendPhone + "'" + " and sendPhone=" + "'" + receivePhone + "'"
                    + " group by date(sendDate)");
        }
        List<SmsExpandInfo> smsExpandInfoList = new ArrayList<>();
        if (smsInfoList.size() > 0) {
            for (SmsInfo smsInfo : smsInfoList) {
                SmsExpandInfo smsExpandInfo = new SmsExpandInfo();
                String dateDateStr = smsInfo.getSendDate();
                smsExpandInfo.setSmsTitle(StringUtils.friendly_time2(dateDateStr));
                List<SmsInfo> smsInfoListChildren = smsDatabase.queryWhere(" where sendPhone=" + AppConfig.SENDPHONE + " and receivePhone=" + "'" + receivePhone + "'"
                                + " or receivePhone=" + AppConfig.SENDPHONE + " and sendPhone=" + "'" + receivePhone + "'",
                        " where sendDate=" + "'" + dateDateStr + "'" + " and hidePhone!=" + AppConfig.SENDPHONE);
                if (smsInfoListChildren.size() <= 0) {
                    smsInfoListChildren = smsDatabase.queryWhere(" where sendPhone=" + "'" + sendPhone + "'" + " and receivePhone=" + "'" + receivePhone + "'"
                                    + " or receivePhone=" + "'" + sendPhone + "'" + " and sendPhone=" + "'" + receivePhone + "'",
                            " where sendDate=" + "'" + dateDateStr + "'" + " and hidePhone!=" + AppConfig.SENDPHONE);
                }
                smsExpandInfo.setSmsInfos(smsInfoListChildren);
                smsExpandInfoList.add(smsExpandInfo);
            }

            smsDetailsExpandAdapter = new SMSDetailsExpandAdapter(this, smsExpandInfoList);
        }
        lv_sms_details.setAdapter(smsDetailsExpandAdapter);
        for (int i = 0; i < smsExpandInfoList.size(); i++) {
            lv_sms_details.expandGroup(i);
        }
        lv_sms_details.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        lv_sms_details.setSelection(ExpandableListView.FOCUS_DOWN);//使列表显示最底部

        initOnItemClick(smsExpandInfoList);
    }

    //列表点击事件
    public void initOnItemClick(final List<SmsExpandInfo> smsExpandInfoList) {
        lv_sms_details.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //startActivity(FileUtils.getImageFileIntent(AppConfig.DEFAULT_SAVE_FILE_PATH+"1.jpg"));
                final SmsInfo smsInfo = smsExpandInfoList.get(groupPosition).getSmsInfos().get(childPosition);
                if (!StringUtils.isEmpty(smsInfo.getFileName())) {//文件操作 打开文件
                    FileUtils.openFile(SMSDetailsActivity.this, smsInfo.getFilePath());
                }
                return true;
            }
        });
        lv_sms_details.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                int groupPosition = (Integer) view.getTag(R.id.tv_title);
                int childPosition = (Integer) view.getTag(R.id.ll_child);
                if (childPosition != -1) {
                    final SmsInfo smsInfo = smsExpandInfoList.get(groupPosition).getSmsInfos().get(childPosition);
                    if (!StringUtils.isEmpty(smsInfo.getFileName())) {//文件操作
                        final String[] arrays = {"删除", "导出"};
                        DialogHelp.getSingleChoiceDialog(SMSDetailsActivity.this, "选择操作", arrays, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    smsDatabase.delete(smsInfo.getSid());
                                    initApapter();
                                    File file = new File(smsInfo.getFilePath());
                                    file.delete();
                                } else {
                                    ExportAsyncTask exportAsyncTask = new ExportAsyncTask(SMSDetailsActivity.this, smsInfo.getFilePath());
                                    exportAsyncTask.execute();
                                }
                                dialogInterface.dismiss();
                            }
                        }).show();
                    } else {//消息操作
                        final String[] arrays = {"删除"};
                        DialogHelp.getSingleChoiceDialog(SMSDetailsActivity.this, "选择操作", arrays, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    smsDatabase.delete(smsInfo.getSid());
                                    initApapter();
                                }
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }
//                   Toast.makeText(SMSDetailsActivity.this,smsInfo.getFileName(),Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    //初始化底部文件选择窗口
    public void initInputRecycleView() {
        final String[] titles = {"文档", "图片", "音频", "视频"};
        final int[] images = {R.drawable.file_document, R.drawable.file_picture, R.drawable.file_music, R.drawable.file_video};
        FileInputAdapter fileInputAdapter = new FileInputAdapter(this, titles, images);
        rcy_input_list.setAdapter(fileInputAdapter);
        rcy_input_list.setLayoutManager(new GridLayoutManager(this, 4));
        fileInputAdapter.setOnItemClickLitener(new FileInputAdapter.OnItemClickLitener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent = new Intent(SMSDetailsActivity.this, SelectedFilesActivity.class);
                intent.putExtra("type", titles[position]);
                startActivityForResult(intent, 0);
            }
        });
    }

    //多文件文件选择，异步任务执行导入操作
    public void initImportFiles(Bundle bundle) {
        fileSelectedList = (List<FileSelectedInfo>) bundle.getSerializable("fileInfoList");
        if (fileSelectedList != null && fileSelectedList.size() > 0) {
            for (FileSelectedInfo fileInfo : fileSelectedList) {
                ImportAsyncTask importAsyncTask = new ImportAsyncTask(this, fileInfo);
                importAsyncTask.execute();
            }
        }
    }

    //从文件夹选择文件，异步任务执行导入操作
    public void initImportFolderFiles(String selectedFilePath) {
        //从系统文件夹返回的文件
        File file = new File(selectedFilePath);
        FileSelectedInfo fileSelectedInfo = new FileSelectedInfo();
        fileSelectedInfo.setFileName(file.getName());
        fileSelectedInfo.setFilePath(file.getAbsolutePath());
        fileSelectedInfo.setFilesize(FileUtils.formatFileSize(file.length()));
        java.util.Date date = new java.util.Date(file.lastModified());//文件最后修改时间
        fileSelectedInfo.setFileTime(StringUtils.getDateString(date));
        ImportAsyncTask importAsyncTask = new ImportAsyncTask(this, fileSelectedInfo);
        importAsyncTask.execute();
    }

    /**
     * 异步任务文件导入操作
     */
    private class ImportAsyncTask extends AsyncTask<Void, Integer, Void> {
        Context contex;
        FileSelectedInfo fileSelectedInfo;
        SmsInfo smsInfo;
        String toFilePath;
        File fromFile, toFile;
        long size = 0;

        public ImportAsyncTask(Context contex, FileSelectedInfo fileSelectedInfo) {
            this.contex = contex;
            this.fileSelectedInfo = fileSelectedInfo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendData(fileSelectedInfo);
            tv_progress.setVisibility(View.VISIBLE);
            smsInfo = smsDatabase.queryWhereLastData();
            smsInfoList.add(smsInfo);
            // 更新界面
            smsDetailsExpandAdapter.notifyDataSetChanged();
            File appDir = new File(contex.getFilesDir(), "import");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            toFile = new File(appDir, smsInfo.getFileName());
            toFilePath = toFile.getAbsolutePath();
            fromFile = new File(smsInfo.getFilePath());
            size = fromFile.length();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /**
             * 复制单个文件
             * @param oldPath String 原文件路径
             * @param newPath String 复制后路径
             * @return boolean
             */
            try {
                int byteread = 0;
                if (fromFile.exists()) { //文件存在时
                    InputStream inStream = new FileInputStream(fromFile); //读入原文件
                    FileOutputStream fs = new FileOutputStream(toFile);
                    byte[] buffer = new byte[1024];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        System.out.println(byteread);
                        fs.write(buffer, 0, byteread);
                        //计算复制进度
                        int i = (int) ((100 * toFile.length() / size));
                        publishProgress(i);
                    }
                    inStream.close();
                }
            } catch (Exception e) {
                System.out.println("导入文件操作出错");
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv_progress.setText("已导入" + values[0] + "%");
            if (values[0] >= 100) {
                tv_progress.setText("已完成");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (toFile.length() == size) {
                smsInfo.setIsSuccess(1);
                smsInfo.setFilePath(toFilePath);
            } else {
                smsInfo.setIsSuccess(0);
            }
            smsDatabase.updateFileState(smsInfo);
            tv_progress.setText("");
            tv_progress.setVisibility(View.GONE);
            initApapter();
        }
    }

    /**
     * 异步任务文件导出操作
     */
    public class ExportAsyncTask extends AsyncTask<Void, Integer, Void> {
        Context contex;
        String fromFilePath;
        long size = 0;
        File fromFile, toFile;

        public ExportAsyncTask(Context contex, String fromFilePath) {
            this.contex = contex;
            this.fromFilePath = fromFilePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tv_progress.setVisibility(View.VISIBLE);
            File appDir = new File(AppConfig.DEFAULT_SAVE_FILE_PATH);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            String[] fromFilePaths = fromFilePath.split("/");
            toFile = new File(appDir, fromFilePaths[fromFilePaths.length - 1]);
            fromFile = new File(fromFilePath);
            size = fromFile.length();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /**
             * 复制单个文件
             * @param oldPath String 原文件路径
             * @param newPath String 复制后路径
             * @return boolean
             */
            try {
                int byteread = 0;
                if (fromFile.exists()) { //文件存在时
                    InputStream inStream = new FileInputStream(fromFile); //读入原文件
                    FileOutputStream fs = new FileOutputStream(toFile);
                    byte[] buffer = new byte[1024];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        System.out.println(byteread);
                        fs.write(buffer, 0, byteread);
                        //计算复制进度
                        int i = (int) ((100 * toFile.length() / size));
                        publishProgress(i);
                    }
                    inStream.close();
                }
            } catch (Exception e) {
                System.out.println("导出文件操作出错");
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv_progress.setText("已导出" + values[0] + "%");
            if (values[0] >= 100) {
                tv_progress.setText("已完成");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv_progress.setVisibility(View.GONE);
            tv_progress.setText("");
            Toast.makeText(SMSDetailsActivity.this, "导出完成", Toast.LENGTH_SHORT).show();
        }
    }

    //页面跳转返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            switch (requestCode) {
                case 0:
                    Bundle bundle = data.getExtras();
                    initImportFiles(bundle);
                    break;

            }
        } else if (resultCode == 300) {
            String selectedFilePath = data.getStringExtra("selectedFilePath");
            initImportFolderFiles(selectedFilePath);
        }
    }

    //返回键点击
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isOpenInputFile) {
                rcy_input_list.setVisibility(View.GONE);
                isOpenInputFile = true;
                return true;
            } else {
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
