package com.example.smsmanager.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsmanager.AppManager;
import com.example.smsmanager.R;
import com.example.smsmanager.adapter.FileSelectedListAdapter;
import com.example.smsmanager.bean.FileInfo;
import com.example.smsmanager.bean.FileSelectedInfo;
import com.example.smsmanager.utils.FileUtils;
import com.example.smsmanager.utils.ImageUtils;
import com.example.smsmanager.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/3/10.
 * 选择文件界面
 */

public class SelectedFilesActivity extends AppCompatActivity implements View.OnClickListener, FileSelectedListAdapter.OnItemClickLitener {
    ImageView iv_back;
    TextView tv_title;
    RecyclerView rcl_file_list;
    TextView tv_selected_count;
    Button btn_send;
    View ll_no_file;
    TextView tv_folder;
    View ll_progress;
    private AsyncQueryHandler asyncQueryHandler;//异步查询数据库类对象加载更多
    private List<FileInfo> fileInfoList = new ArrayList<>();
    FileSelectedListAdapter adapter;
    String type;//文件类型
    //上拉加载更多
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private int lastVisibleItem;
    private String layoutType;
    int pageSize =20;//每页几条
    int pageNum =0;//第几页
    int pageCount;//总页数
    Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_files);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }

    public void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rcl_file_list = (RecyclerView) findViewById(R.id.rcl_file_list);
        tv_selected_count = (TextView) findViewById(R.id.tv_selected_count);
        btn_send = (Button) findViewById(R.id.btn_send);
        ll_no_file = findViewById(R.id.ll_no_file);
        tv_folder = (TextView) findViewById(R.id.tv_folder);
        ll_progress= findViewById(R.id.ll_progress);
        type = getIntent().getStringExtra("type");
        tv_title.setText("选择" + type);
        // 实例化
        asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
    }

    public void initData() {
        iv_back.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        tv_folder.setOnClickListener(this);
        ShowFileAsyncTask showFileAsyncTask=new ShowFileAsyncTask(this);
        showFileAsyncTask.execute();
    }
    public void initFilesShow(int pageNum){
        String[] projection=null;
        String[] selectionArges=null;
        String selection=null;
        switch (type) {
            case "文档":
                uri = FileUtils.uriFiles;
                projection = FileUtils.projectionFiles;
                //构造筛选语句
                selection =MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";
                selectionArges = new String[]{"text/plain","application/msword"};
                break;
            case "图片":
                pageSize=28;
                uri = FileUtils.uriImages;
                projection = FileUtils.projectionImages;
                break;
            case "音频":
                uri = FileUtils.uriAudio;
                projection = FileUtils.projectionAudio;
                break;
            case "视频":
                uri = FileUtils.uriVideo;
                projection = FileUtils.projectionVideo;
                break;
        }
        // 按照sort_key升序查詢
        pageCount=getAllCounts(uri)/pageSize+1;//计算总页数
        String limit =" _id asc limit " +pageSize*pageNum+ "," +pageSize;
        // 查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, selection, selectionArges, limit);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_folder:
                //startActivityForResult(new Intent(SelectedFilesActivity.this, FileListActivity.class),1);
                //打开系统文件管理器，选择文件
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(intent2,0);
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivityForResult(new Intent(SelectedFilesActivity.this, FileListActivity.class),1);
                }
                break;
            case R.id.btn_send:
                ArrayList<FileSelectedInfo> fileSelectedList = new ArrayList<>();//选中的选项
                for (FileInfo fileInfo : fileInfoList) {
                    if (fileInfo.isChoosed()) {
                        FileSelectedInfo fileSelectedInfo=new FileSelectedInfo();
                        fileSelectedInfo.setFileName(fileInfo.getFileName());
                        fileSelectedInfo.setFilePath(fileInfo.getFilePath());
                        fileSelectedInfo.setFilesize(fileInfo.getFilesize());
                        fileSelectedInfo.setFileTime(fileInfo.getFileTime());
                        fileSelectedList.add(fileSelectedInfo);
                    }
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileInfoList",fileSelectedList);
                intent.putExtras(bundle);
                setResult(200, intent);
                finish();
                break;

        }
    }
    //加载适配器
    private void setAdapter(List<FileInfo> list) {
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager=new GridLayoutManager(this,4);
        if (list.size() > 0) {
            ll_no_file.setVisibility(View.GONE);
            rcl_file_list.setVisibility(View.VISIBLE);
        } else {
            ll_no_file.setVisibility(View.VISIBLE);
            rcl_file_list.setVisibility(View.GONE);
        }

        adapter = new FileSelectedListAdapter(this, list, type);
        rcl_file_list.setAdapter(adapter);
        if (type.equals("图片")) {
            layoutType="grid";
            rcl_file_list.setLayoutManager(gridLayoutManager);
        } else {
            layoutType="linear";
            rcl_file_list.setLayoutManager(linearLayoutManager);
        }
        adapter.setOnItemClickLitener(this);
        loadMore();//分页上拉加载更多
    }

    /**
     * 获得系统联系人的所有记录数目
     * @return
     */
    public int getAllCounts(Uri uri){
        // 使用ContentResolver查找联系人数据
        Cursor cursor = this.getContentResolver().query(uri, null, null,
                null, null);
        return cursor.getCount();
    }
    @Override
    public void OnItemClick(View view, int position) {
        FileInfo fileInfo = fileInfoList.get(position);
        if (fileInfo.isChoosed() == true) {
            fileInfo.setChoosed(false);
        } else {
            fileInfo.setChoosed(true);
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
        long toatleSize = 0;
        //总计所有价格
        for (int i = 0; i < fileInfoList.size(); i++) {
            FileInfo fileInfo = fileInfoList.get(i);
            if (fileInfo.isChoosed()) {
                selectedCount++;
                toatleSize += fileInfo.getFilesizeSmall();
            }
        }
        if(selectedCount<=0){
            tv_selected_count.setText("已选 " +"0B");
        }else {
            tv_selected_count.setText("已选 " + FileUtils.formatFileSize(toatleSize));
        }
        btn_send.setText("发送(" + selectedCount + ")");
    }

    /**
     * 获取文件列表
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {
        ContentResolver resolver;

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
            resolver = cr;
        }
        //异步文件查询操作
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            List<FileInfo> queryFilelist=new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String title = cursor.getString(1);
                    String display_name = cursor.getString(2);
                    String dataPath = cursor.getString(3);
                    long size = cursor.getLong(4);
                    String mimeType=cursor.getString(5);
                    FileInfo fileInfo = new FileInfo();
                    //文件名称
                    String[] dataPaths = dataPath.split("/");
                    if(StringUtils.isEmpty(display_name)){
                        display_name=dataPaths[dataPaths.length - 1];
                    }
                    String[] display_names = display_name.split("/");
                    fileInfo.setFileName(display_names[display_names.length - 1]);
                    //文件大小
                    fileInfo.setFilesize(FileUtils.formatFileSize(size));
                    //文件大小单位
                    fileInfo.setFilesizeSmall(size);
                    //文件创建时间
                    File file = new File(dataPath);
                    Date date = new Date(file.lastModified());//文件最后修改时间
                    fileInfo.setFileTime(StringUtils.getDateString(date));
                    //文件路径
                    fileInfo.setFilePath(dataPath);
                    switch (type) {
                        case "文档":
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_file);
                            fileInfo.setThumbnail(ImageUtils.drawableToBitmap(drawable));
                            break;
                        case "图片":
                            Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                            fileInfo.setThumbnail(thumbnail);
                            break;
                        case "音频":
                            //获取专辑封面（如果数据量大的话，会很耗时——需要考虑如何开辟子线程加载）
                            Bitmap albumArt = FileUtils.createAlbumArt(dataPath);
                            fileInfo.setThumbnail(albumArt);
                            break;
                        case "视频":
                            //获取缩略图（如果数据量大的话，会很耗时——需要考虑如何开辟子线程加载）
                        /*
                         * 可以访问android.provider.MediaStore.Video.Thumbnails查询图片缩略图
                         * Thumbnails下的getThumbnail方法可以获得图片缩略图，其中第三个参数类型还可以选择MINI_KIND
                        */
                            Bitmap thumbnail2 = MediaStore.Video.Thumbnails.getThumbnail(resolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                            fileInfo.setThumbnail(thumbnail2);
                            break;
                    }
                    queryFilelist.add(fileInfo);
                }
            }
            fileInfoList.addAll(queryFilelist);
            adapter.notifyDataSetChanged();
            super.onQueryComplete(token, cookie, cursor);
        }
    }
    //显示文件列表
    private class ShowFileAsyncTask extends AsyncTask<Void, Void, Void>{
        Cursor cursor = null;
        Context context;
        String[] projection=null;
        String[] selectionArges=null;
        String selection=null;
        String limit=null;
        ContentResolver resolver;
        public  ShowFileAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_progress.setVisibility(View.VISIBLE);
            fileInfoList.clear();
            switch (type) {
                case "文档":
                    uri = FileUtils.uriFiles;
                    projection = FileUtils.projectionFiles;
                    //构造筛选语句
                    selection =MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                            + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";
                    selectionArges = new String[]{"text/plain","application/msword"};
                    break;
                case "图片":
                    pageSize=28;
                    uri = FileUtils.uriImages;
                    projection = FileUtils.projectionImages;
                    break;
                case "音频":
                    uri = FileUtils.uriAudio;
                    projection = FileUtils.projectionAudio;
                    break;
                case "视频":
                    uri = FileUtils.uriVideo;
                    projection = FileUtils.projectionVideo;
                    break;
            }
            // 按照sort_key升序查詢
            pageCount=getAllCounts(uri)/pageSize+1;//计算总页数
            limit =" _id asc limit " +0+ "," +pageSize;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArges, limit);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        String title = cursor.getString(1);
                        String display_name = cursor.getString(2);
                        String dataPath = cursor.getString(3);
                        long size = cursor.getLong(4);
                        String mimeType = cursor.getString(5);
                        FileInfo fileInfo = new FileInfo();
                        //文件名称
                        String[] dataPaths = dataPath.split("/");
                        if (StringUtils.isEmpty(display_name)) {
                            display_name = dataPaths[dataPaths.length - 1];
                        }
                        String[] display_names = display_name.split("/");
                        fileInfo.setFileName(display_names[display_names.length - 1]);
                        //文件大小
                        fileInfo.setFilesize(FileUtils.formatFileSize(size));
                        //文件大小单位
                        fileInfo.setFilesizeSmall(size);
                        //文件创建时间
                        File file = new File(dataPath);
                        Date date = new Date(file.lastModified());//文件最后修改时间
                        fileInfo.setFileTime(StringUtils.getDateString(date));
                        //文件路径
                        fileInfo.setFilePath(dataPath);
                        switch (type) {
                            case "文档":
                                Drawable drawable = getResources().getDrawable(R.drawable.ic_file);
                                fileInfo.setThumbnail(ImageUtils.drawableToBitmap(drawable));
                                break;
                            case "图片":
                                Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
                                fileInfo.setThumbnail(thumbnail);
                                break;
                            case "音频":
                                //获取专辑封面（如果数据量大的话，会很耗时——需要考虑如何开辟子线程加载）
                                Bitmap albumArt = FileUtils.createAlbumArt(dataPath);
                                fileInfo.setThumbnail(albumArt);
                                break;
                            case "视频":
                                //获取缩略图（如果数据量大的话，会很耗时——需要考虑如何开辟子线程加载）
                        /*
                         * 可以访问android.provider.MediaStore.Video.Thumbnails查询图片缩略图
                         * Thumbnails下的getThumbnail方法可以获得图片缩略图，其中第三个参数类型还可以选择MINI_KIND
                        */
                                Bitmap thumbnail2 = MediaStore.Video.Thumbnails.getThumbnail(resolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                                fileInfo.setThumbnail(thumbnail2);
                                break;
                        }
                        fileInfoList.add(fileInfo);
                    }
                }
            }catch (Exception e) {
                Log.e("SekectedContactsActivity", "error:", e);
            } finally {
                cursor.close();
            }
            cursor.close();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setAdapter(fileInfoList);
            ll_progress.setVisibility(View.GONE);
        }
    }
    //上拉加载类
    public void loadMore(){
        rcl_file_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == fileInfoList.size()-1){
                    pageNum++;
                    //模拟网络请求
                    if (pageNum > pageCount-1) {
                        //模拟共有pageCount条数据
                        Toast.makeText(SelectedFilesActivity.this, "没有更多数据!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        initFilesShow(pageNum);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(layoutType.equals("linear")){
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }else{
                    lastVisibleItem=gridLayoutManager.findLastVisibleItemPosition();
                }

            }

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String selectedFilePath = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Uri uri = data.getData();
                    selectedFilePath=FileUtils.getPath(SelectedFilesActivity.this, uri);
                    Log.i("SelectedFilesActivity", "------->" + selectedFilePath);
                    break;
                case 1:
                    selectedFilePath=data.getStringExtra("selectedFilePath");
                    break;
            }
            Intent intent=new Intent();
            intent.putExtra("selectedFilePath",selectedFilePath);
            setResult(300,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
