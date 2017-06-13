package com.example.smsmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsmanager.AppConfig;
import com.example.smsmanager.R;
import com.example.smsmanager.bean.SmsExpandInfo;
import com.example.smsmanager.bean.SmsInfo;
import com.example.smsmanager.utils.ImageUtils;
import com.example.smsmanager.utils.StringUtils;

import java.util.List;

/**
 * Created by admin on 2017/3/2.
 *  消息详情适配器
 */

public class SMSDetailsExpandAdapter extends BaseExpandableListAdapter{
    private Context context;
    private List<SmsExpandInfo> smsExpandInfoList;
    public SMSDetailsExpandAdapter(Context context,List<SmsExpandInfo> smsExpandInfoList){
        this.context=context;
        this.smsExpandInfoList=smsExpandInfoList;
    }
    @Override
    public int getGroupCount() {
        return smsExpandInfoList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return smsExpandInfoList.get(groupPosition).getSmsInfos().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (smsExpandInfoList == null) {
            return null;
        }
        return smsExpandInfoList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        SmsExpandInfo smsExpandInfo=smsExpandInfoList.get(groupPosition);
        if (smsExpandInfo == null || smsExpandInfo.getSmsInfos()== null
                || smsExpandInfo.getSmsInfos().isEmpty()) {
            return null;
        }
        return smsExpandInfo.getSmsInfos().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SmsExpandInfo smsExpandInfo=smsExpandInfoList.get(groupPosition);
        GroupViewHolder groupViewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_expand_sms_details_title, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        if (smsExpandInfo != null) {
            groupViewHolder.tv_title.setText(smsExpandInfo.getSmsTitle());
        }
        //通过打标记的方法来获取groupPosition和childPosition
        convertView.setTag(R.id.tv_title, groupPosition);
        convertView.setTag(R.id.ll_child, -1);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        ViewHolder3 viewHolder3 = null;
        ViewHolder4 viewHolder4 = null;
        SmsExpandInfo supplierInfo=(SmsExpandInfo)getGroup(groupPosition);
        SmsInfo smsInfo=supplierInfo.getSmsInfos().get(childPosition);
        if (null == convertView) {
            switch (getChildType(groupPosition,childPosition)){
                case 0:
                    convertView=View.inflate(context, R.layout.item_expand_sms_details_left, null);
                    viewHolder1 = new ViewHolder1();
                    viewHolder1.tv_sms_content=(TextView) convertView.findViewById(R.id.tv_sms_content);
                    viewHolder1.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
                    convertView.setTag(viewHolder1);
                    break;
                case 1:
                    convertView=View.inflate(context, R.layout.item_expand_sms_details_right, null);
                    viewHolder2 = new ViewHolder2();
                    viewHolder2.tv_sms_content=(TextView) convertView.findViewById(R.id.tv_sms_content);
                    viewHolder2.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
                    convertView.setTag(viewHolder2);
                    break;
                case 2:
                    convertView=View.inflate(context, R.layout.item_expand_sms_details_file_left, null);
                    viewHolder3 = new ViewHolder3();
                    viewHolder3.file_name=(TextView) convertView.findViewById(R.id.file_name);
                    viewHolder3.file_size=(TextView) convertView.findViewById(R.id.file_size);
                    viewHolder3.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
                    viewHolder3.tv_file_state=(TextView)convertView.findViewById(R.id.tv_file_state);
                    viewHolder3.iv_image=(ImageView) convertView.findViewById(R.id.iv_image);
                    convertView.setTag(viewHolder3);
                    break;
                case 3:
                    convertView=View.inflate(context, R.layout.item_expand_sms_details_file_right, null);
                    viewHolder4 = new ViewHolder4();
                    viewHolder4.file_name=(TextView) convertView.findViewById(R.id.file_name);
                    viewHolder4.file_size=(TextView) convertView.findViewById(R.id.file_size);
                    viewHolder4.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
                    viewHolder4.tv_file_state=(TextView)convertView.findViewById(R.id.tv_file_state);
                    viewHolder4.iv_image=(ImageView) convertView.findViewById(R.id.iv_image);
                    convertView.setTag(viewHolder4);
                    break;
            }

        } else {
            switch (getChildType(groupPosition,childPosition)){
                case 0:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
                case 1:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    break;
                case 2:
                    viewHolder3 = (ViewHolder3) convertView.getTag();
                    break;
                case 3:
                    viewHolder4 = (ViewHolder4) convertView.getTag();
                    break;
            }

        }

        String sendTime=smsInfo.getSendTime();
        String fileType=smsInfo.getFileType();
        Drawable drawable;
        switch (getChildType(groupPosition,childPosition)){
            case 0:
                viewHolder1.tv_sms_content.setText(smsInfo.getSmsContent());
                viewHolder1.tv_time.setText(sendTime);
                break;
            case 1:
                viewHolder2.tv_sms_content.setText(smsInfo.getSmsContent());
                viewHolder2.tv_time.setText(sendTime);
                break;
            case 2:
                viewHolder3.file_name.setText(smsInfo.getFileName());
                viewHolder3.file_size.setText(smsInfo.getFileSize());
                viewHolder3.tv_time.setText(sendTime);
                switch (fileType){
                    case "image":
                        drawable=context.getResources().getDrawable(R.drawable.ic_picture);
                        viewHolder3.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "audio":
                        drawable=context.getResources().getDrawable(R.drawable.ic_music);
                        viewHolder3.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "video":
                        drawable=context.getResources().getDrawable(R.drawable.ic_video);
                        viewHolder3.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "text":
                        drawable=context.getResources().getDrawable(R.drawable.ic_files);
                        viewHolder3.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    default:
                        drawable=context.getResources().getDrawable(R.drawable.ic_files);
                        viewHolder3.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                }

                viewHolder3.tv_file_state.setText("正在导入...");
                if(smsInfo.getIsSuccess()==1){
                    viewHolder3.tv_file_state.setText("已完成");
                    viewHolder3.tv_file_state.setTextColor(context.getResources().getColor(R.color.text_555));
                }else if(smsInfo.getIsSuccess()==0){
                    viewHolder3.tv_file_state.setText("失败");
                    viewHolder3.tv_file_state.setTextColor(Color.RED);
                }else{
                    viewHolder3.tv_file_state.setText("正在导入...");
                    viewHolder3.tv_file_state.setTextColor(context.getResources().getColor(R.color.text_555));
                }
                break;
            case 3:
                viewHolder4.file_name.setText(smsInfo.getFileName());
                viewHolder4.file_size.setText(smsInfo.getFileSize());
                viewHolder4.tv_time.setText(sendTime);
                switch (fileType){
                    case "image":
                        drawable=context.getResources().getDrawable(R.drawable.ic_picture);
                        viewHolder4.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "audio":
                        drawable=context.getResources().getDrawable(R.drawable.ic_music);
                        viewHolder4.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "video":
                        drawable=context.getResources().getDrawable(R.drawable.ic_video);
                        viewHolder4.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    case "text":
                        drawable=context.getResources().getDrawable(R.drawable.ic_files);
                        viewHolder4.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                    default:
                        drawable=context.getResources().getDrawable(R.drawable.ic_files);
                        viewHolder4.iv_image.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
                        break;
                }
                viewHolder4.tv_file_state.setText("正在导入...");
                if(smsInfo.getIsSuccess()==1){
                   viewHolder4.tv_file_state.setText("已完成");
                    viewHolder4.tv_file_state.setTextColor(context.getResources().getColor(R.color.text_555));
                }else if(smsInfo.getIsSuccess()==0){
                    viewHolder4.tv_file_state.setText("失败");
                    viewHolder4.tv_file_state.setTextColor(Color.RED);
                }else{
                    viewHolder4.tv_file_state.setText("正在导入...");
                    viewHolder4.tv_file_state.setTextColor(context.getResources().getColor(R.color.text_555));
                }
                break;
        }
        //通过打标记的方法来获取groupPosition和childPosition
        convertView.setTag(R.id.tv_title, groupPosition);
        convertView.setTag(R.id.ll_child, childPosition);
        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        SmsInfo smsInfo= (SmsInfo) getChild(groupPosition,childPosition);
        if(smsInfo.getReceivePhone().equals(AppConfig.SENDPHONE)){
            if(!StringUtils.isEmpty(smsInfo.getFileName())){
                return 2;
            }else{
                return 0;
            }
        }else if(smsInfo.getSendPhone().equals(AppConfig.SENDPHONE)) {
            if(!StringUtils.isEmpty(smsInfo.getFileName())){
                return 3;
            }else{
                return 1;
            }
        }
        return -1;
    }

    @Override
    public int getChildTypeCount() {
        return 4;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class GroupViewHolder {
        TextView tv_title;
    }

    public class ViewHolder1{
        TextView tv_sms_content,tv_time;
    }
    public class ViewHolder2 {
        TextView tv_sms_content,tv_time;
    }
    public class ViewHolder3{
        TextView file_name,file_size,tv_time,tv_file_state;
        ImageView iv_image;
    }
    public class ViewHolder4{
        TextView file_name,file_size,tv_time,tv_file_state;
        ImageView iv_image;
    }
}
