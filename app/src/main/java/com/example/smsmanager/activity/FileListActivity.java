package com.example.smsmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsmanager.R;
import com.example.smsmanager.bean.FileListEntity;
import com.example.smsmanager.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dong on 2017/3/10.
 * 文件列表页面
 */

public class FileListActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back;
    private ListView mListView;
    TextView tv_sure;
    private MyFileAdapter mAdapter;
    private Context mContext;
    private File currentFile;
    String sdRootPath;

    private ArrayList<FileListEntity> mList;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if(mAdapter ==null){
                            mAdapter = new MyFileAdapter(mContext, mList);
                            mListView.setAdapter(mAdapter);
                        }else{
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        };

        mContext = this;
        mList = new ArrayList<>();
        sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentFile = new File(sdRootPath);
        System.out.println(sdRootPath);
        initView();
        getData(sdRootPath);


    }

    @Override
    public void onBackPressed() {
//      super.onBackPressed();
        System.out.println("onBackPressed...");
        if(sdRootPath.equals(currentFile.getAbsolutePath())){
            System.out.println("已经到了根目录...");
            finish();
            return ;
        }

        String parentPath = currentFile.getParent();
        currentFile = new File(parentPath);
        getData(parentPath);
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        mListView = (ListView) findViewById(R.id.listView1);
        tv_sure= (TextView) findViewById(R.id.tv_sure);
        iv_back.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        String type=getIntent().getStringExtra("type");
        if(!StringUtils.isEmpty(type)&&type.equals("setting")){
            tv_sure.setVisibility(View.VISIBLE);
        }else{
            tv_sure.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final FileListEntity entity = mList.get(position);
                if(entity.getFileType() == FileListEntity.Type.FLODER){
                    currentFile = new File(entity.getFilePath());
                    getData(entity.getFilePath());
                }else if(entity.getFileType() == FileListEntity.Type.FILE){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent();
                            intent.putExtra("selectedFilePath",entity.getFilePath());
                            setResult(RESULT_OK,intent);
                            finish();
                           // Toast.makeText(mContext, entity.getFilePath()+"  "+entity.getFileName(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void getData(final String path) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                findAllFiles(path);
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sure:
                Intent intent=new Intent();
                intent.putExtra("currentFilePath",currentFile.getAbsolutePath());
                setResult(200,intent);
                finish();
                break;
            default:
                break;
        }

    }

    /**
     * 查找path地址下所有文件
     * @param path
     */
    public void findAllFiles(String path) {
        mList.clear();

        if(path ==null ||path.equals("")){
            return;
        }
        File fatherFile = new File(path);
        File[] files = fatherFile.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                FileListEntity entity = new FileListEntity();
                boolean isDirectory = files[i].isDirectory();
                if(isDirectory ==true){
                    entity.setFileType(FileListEntity.Type.FLODER);
//                  entity.setFileName(files[i].getPath());
                }else{
                    entity.setFileType(FileListEntity.Type.FILE);
                }
                entity.setFileName(files[i].getName().toString());
                entity.setFilePath(files[i].getAbsolutePath());
                entity.setFileSize(files[i].length()+"");
                mList.add(entity);
            }
        }
        mHandler.sendEmptyMessage(1);

    }


    class MyFileAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<FileListEntity> mAList;
        private LayoutInflater mInflater;



        public MyFileAdapter(Context mContext, ArrayList<FileListEntity> mList) {
            super();
            this.mContext = mContext;
            this.mAList = mList;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mAList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mAList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if(mAList.get(position).getFileType() == FileListEntity.Type.FLODER){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//          System.out.println("position-->"+position+"    ---convertView--"+convertView);
            ViewHolder holder = null;
            int type = getItemViewType(position);
            FileListEntity entity = mAList.get(position);

            if(convertView == null){
                holder = new ViewHolder();
                switch (type) {
                    case 0://folder
                        convertView = mInflater.inflate(R.layout.item_listview, parent, false);
                        holder.iv = (ImageView) convertView.findViewById(R.id.item_imageview);
                        holder.tv = (TextView) convertView.findViewById(R.id.item_textview);
                        break;
                    case 1://file
                        convertView = mInflater.inflate(R.layout.item_listview, parent, false);
                        holder.iv = (ImageView) convertView.findViewById(R.id.item_imageview);
                        holder.tv = (TextView) convertView.findViewById(R.id.item_textview);

                        break;

                    default:
                        break;

                }
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (type) {
                case 0:
                    holder.iv.setImageResource(R.drawable.ic_folder);
                    holder.tv.setText(entity.getFileName());
                    break;
                case 1:
                    holder.iv.setImageResource(R.drawable.ic_file);
                    holder.tv.setText(entity.getFileName());
                    break;
                default:
                    break;
            }


            return convertView;
        }

    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}
