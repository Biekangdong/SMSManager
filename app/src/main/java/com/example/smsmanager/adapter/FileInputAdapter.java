package com.example.smsmanager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsmanager.R;
import com.example.smsmanager.utils.ImageUtils;

/**
 * Created by admin on 2017/3/8.
 * 文件列表适配器
 */

public class FileInputAdapter extends RecyclerView.Adapter<FileInputAdapter.MyViewHolder>{
    private Context context;
    String[] titles;
    int[] images;
    public OnItemClickLitener onItemClickLitener;
    public FileInputAdapter(Context context,String[] titles,int[] images){
        this.context=context;
        this.titles=titles;
        this.images=images;
    }
    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_file,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Drawable drawable=context.getResources().getDrawable(images[position]);
        Bitmap bitmap= ImageUtils.drawableToBitmap(drawable);
        holder.iv_image.setImageBitmap(bitmap);
        holder.tv_title.setText(titles[position]);
        if (onItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.OnItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_image;
        TextView tv_title;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image= (ImageView) itemView.findViewById(R.id.iv_image);
            tv_title= (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
    public interface OnItemClickLitener {
        //点击
        void OnItemClick(View view, int position);
    }

}
