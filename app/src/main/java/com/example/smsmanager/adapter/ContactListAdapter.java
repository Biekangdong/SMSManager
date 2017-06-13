package com.example.smsmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.smsmanager.R;
import com.example.smsmanager.bean.ContactsInfo;

import java.util.List;

/**
 * Created by admin on 2017/3/1.
 * 联系人适配器
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>{
    private Context context;
    private List<ContactsInfo> contactsInfoList;
    private CheckInterface checkInterface;
    private String type;
    public OnItemClickLitener onItemClickLitener;
    public ContactListAdapter(Context context,List<ContactsInfo> contactsInfoList,String type){
        this.context=context;
        this.contactsInfoList=contactsInfoList;
        this.type=type;
    }
    public void setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_contacts,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ContactsInfo contactsInfo=contactsInfoList.get(position);
        holder.tv_name.setText(contactsInfo.getName());
        holder.tv_phone.setText(contactsInfo.getPhone());
        if(type.equals("selectedContacts")){
            holder.cb_checked.setVisibility(View.VISIBLE);
        }else{
            holder.cb_checked.setVisibility(View.GONE);
        }
        holder.cb_checked.setChecked(contactsInfo.isChoosed());
        holder.cb_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsInfo.setChoosed(((CheckBox)view).isChecked());
                holder.cb_checked.setChecked(((CheckBox)view).isChecked());
                checkInterface.checkChild(position,((CheckBox)view).isChecked());
            }
        });
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
        return contactsInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        TextView tv_phone;
        CheckBox cb_checked;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.tv_name);
            tv_phone=(TextView)itemView.findViewById(R.id.tv_phone);
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
    }

}
