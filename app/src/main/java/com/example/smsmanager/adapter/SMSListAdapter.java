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
import com.example.smsmanager.bean.ContactsInfo;
import com.example.smsmanager.bean.SmsInfo;
import com.example.smsmanager.db.SmsDatabase;

import java.util.List;

/**
 * Created by admin on 2017/3/1.
 *  消息列表适配器
 */

public class SMSListAdapter extends RecyclerView.Adapter<SMSListAdapter.MyViewHolder>{
    private Context context;
    private List<SmsInfo> smsInfoList;
    private CheckInterface checkInterface;
    public OnItemClickLitener onItemClickLitener;
    boolean isLongClick=false;
    boolean isSendPhone;
    public SMSListAdapter(Context context, List<SmsInfo> smsInfoList,boolean isLongClick,boolean isSendPhone){
        this.context=context;
        this.smsInfoList=smsInfoList;
        this.isLongClick=isLongClick;
        this.isSendPhone=isSendPhone;
    }
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    public boolean isSendPhone() {
        return isSendPhone;
    }

    public void setSendPhone(boolean sendPhone) {
        isSendPhone = sendPhone;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_sms,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SmsInfo smsInfo=smsInfoList.get(position);
        if(isSendPhone){
            holder.tv_name.setText(smsInfo.getReceiveName());
        }else{
            holder.tv_name.setText(smsInfo.getSendName());
        }
        holder.tv_sms_content.setText(smsInfo.getSmsContent());
        String sendDate=smsInfo.getSendDate();
        holder.tv_date.setText(sendDate);
        if(isLongClick){
            holder.cb_checked.setVisibility(View.VISIBLE);
        }else{
            holder.cb_checked.setVisibility(View.GONE);
        }
        if(smsInfo.getIsRead().equals("是")){
            holder.iv_read.setVisibility(View.GONE);
        }else{
            holder.iv_read.setVisibility(View.VISIBLE);
        }
        holder.cb_checked.setChecked(smsInfo.isChoosed());
        holder.cb_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsInfo.setChoosed(((CheckBox)view).isChecked());
                holder.cb_checked.setChecked(((CheckBox)view).isChecked());
                checkInterface.checkChild(position,((CheckBox)view).isChecked());
            }
        });

        //item点击
        if (onItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.OnItemClick(holder.itemView, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickLitener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return smsInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_read,iv_user;
        TextView tv_name,tv_sms_content,tv_date;
        CheckBox cb_checked;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_read=(ImageView) itemView.findViewById(R.id.iv_read);
            iv_user=(ImageView) itemView.findViewById(R.id.iv_user);
            tv_name=(TextView) itemView.findViewById(R.id.tv_name);
            tv_sms_content=(TextView) itemView.findViewById(R.id.tv_sms_content);
            tv_date=(TextView) itemView.findViewById(R.id.tv_date);
            cb_checked=(CheckBox)itemView.findViewById(R.id.cb_checked);
        }
    }
    public interface CheckInterface{
        /**
         * 选框状态改变时触发的事件
         * @param position 子元素位置
         * @param isChecked     子元素选中与否
         */
        void checkChild(int position, boolean isChecked);
    }
    public interface OnItemClickLitener {
        //点击
        void OnItemClick(View view, int position);
        //点击
        void OnItemLongClick(View view, int position);
    }

}
