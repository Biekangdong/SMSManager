package com.example.smsmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsmanager.R;
import com.example.smsmanager.bean.FileInfo;

import java.util.List;

/**
 * Created by admin on 2017/3/1.
 * 文件选择适配器
 */

public class FileSelectedListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<FileInfo> fileInfoList;
    private CheckInterface checkInterface;
    public OnItemClickLitener onItemClickLitener;
    private String type;

    public FileSelectedListAdapter(Context context, List<FileInfo> fileInfoList, String type) {
        this.context = context;
        this.fileInfoList = fileInfoList;
        this.type = type;
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals("图片")) {
            position = 0;
        } else {
            position = 1;
        }
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.item_file_picture_selected, parent, false);
                holder = new MyViewHolder(view);
                break;
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.item_file_selected, parent, false);
                holder = new MyViewHolder2(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final FileInfo fileInfo = fileInfoList.get(position);
        switch (getItemViewType(position)) {
            case 0:
                final MyViewHolder myViewHolder = (MyViewHolder) holder;
                if(fileInfo.isChoosed()==true){
                    myViewHolder.v_alpha.setVisibility(View.VISIBLE);
                    myViewHolder.cb_checked.setVisibility(View.VISIBLE);
                }else{
                    myViewHolder.v_alpha.setVisibility(View.GONE);
                    myViewHolder.cb_checked.setVisibility(View.GONE);
                }

                myViewHolder.iv_image.setImageBitmap(fileInfo.getThumbnail());
                if (onItemClickLitener != null) {
                    myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = myViewHolder.getLayoutPosition();
                            onItemClickLitener.OnItemClick(myViewHolder.itemView, position);
                        }
                    });
                }
                break;
            case 1:
                final MyViewHolder2 myViewHolder2 = (MyViewHolder2) holder;
                myViewHolder2.file_name.setText(fileInfo.getFileName());
                myViewHolder2.file_size.setText(fileInfo.getFilesize() + "");
                myViewHolder2.file_time.setText(fileInfo.getFileTime());
                myViewHolder2.cb_checked.setChecked(fileInfo.isChoosed());
                myViewHolder2.iv_image.setImageBitmap(fileInfo.getThumbnail());
//                myViewHolder2.cb_checked.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        fileInfo.setChoosed(((CheckBox) view).isChecked());
//                        myViewHolder2.cb_checked.setChecked(((CheckBox) view).isChecked());
//                        checkInterface.checkChild(position, ((CheckBox) view).isChecked());
//                    }
//                });
                if (onItemClickLitener != null) {
                    myViewHolder2.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = myViewHolder2.getLayoutPosition();
                            onItemClickLitener.OnItemClick(myViewHolder2.itemView, position);
                        }
                    });
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return fileInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        CheckBox cb_checked;
        View  v_alpha;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            cb_checked = (CheckBox) itemView.findViewById(R.id.cb_checked);
            v_alpha=itemView.findViewById(R.id.v_alpha);
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView file_name;
        TextView file_size;
        TextView file_time;
        CheckBox cb_checked;
        public MyViewHolder2(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            file_name = (TextView) itemView.findViewById(R.id.file_name);
            file_size = (TextView) itemView.findViewById(R.id.file_size);
            file_time = (TextView) itemView.findViewById(R.id.file_time);
            cb_checked = (CheckBox) itemView.findViewById(R.id.cb_checked);
        }
    }

    public interface CheckInterface {
        /**
         * 选框状态改变时触发的事件
         *
         * @param position  子元素位置
         * @param isChecked 子元素选中与否
         */
        void checkChild(int position, boolean isChecked);
    }

    public interface OnItemClickLitener {
        //点击
        void OnItemClick(View view, int position);
    }

}
