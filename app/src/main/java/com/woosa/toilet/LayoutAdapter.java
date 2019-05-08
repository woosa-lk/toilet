package com.woosa.toilet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import static com.woosa.toilet.MainActivity.*;

public class LayoutAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String,Object>> item_list;
    Context activity;

    public LayoutAdapter(List<Map<String,Object>> list,Context context){
        this.mInflater = LayoutInflater.from(context);
        item_list = list;
        activity = context;
    }

    @Override
    public int getCount() {
        return item_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder{
        public ImageView img;
        public ImageView img_man_status;
        public ImageView img_woman_status;
        public TextView title;
        public TextView man;
        public TextView woman;
        public TextView busy;
        public TextView free;
        public TextView busy_man;
        public TextView free_man;
        public TextView busy_woman;
        public TextView free_woman;
        public Button viewBtn_check;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_adapter, null);

            holder=new ViewHolder();
            holder.img = convertView.findViewById(R.id.img);
            holder.img_man_status = convertView.findViewById(R.id.img_man_status);
            holder.img_woman_status = convertView.findViewById(R.id.img_woman_status);
            holder.title = convertView.findViewById(R.id.title);
            holder.man = convertView.findViewById(R.id.man);
            holder.woman = convertView.findViewById(R.id.woman);
            holder.busy = convertView.findViewById(R.id.busy);
            holder.free = convertView.findViewById(R.id.free);
            holder.busy_man = convertView.findViewById(R.id.busy_man);
            holder.free_man = convertView.findViewById(R.id.free_man);
            holder.busy_woman = convertView.findViewById(R.id.busy_woman);
            holder.free_woman = convertView.findViewById(R.id.free_woman);
            holder.viewBtn_check = convertView.findViewById(R.id.view_btn_check);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.img.setBackgroundResource((Integer)item_list.get(position).get("img"));
        holder.img_man_status.setBackgroundResource((Integer)item_list.get(position).get("img_man_status"));
        holder.img_woman_status.setBackgroundResource((Integer)item_list.get(position).get("img_woman_status"));
        holder.title.setText((String)item_list.get(position).get("title"));
        holder.man.setText((String)item_list.get(position).get("man"));
        holder.woman.setText((String)item_list.get(position).get("woman"));
        holder.busy.setText((String)item_list.get(position).get("busy"));
        holder.free.setText((String)item_list.get(position).get("free"));
        holder.busy_man.setText((String)item_list.get(position).get("busy_man"));
        holder.free_man.setText((String)item_list.get(position).get("free_man"));
        holder.busy_woman.setText((String)item_list.get(position).get("busy_woman"));
        holder.free_woman.setText((String)item_list.get(position).get("free_woman"));
        holder.viewBtn_check.setOnClickListener(new viewButtonClickListener(position));

        return convertView;
    }

    private class viewButtonClickListener implements View.OnClickListener {
        int position;
        public viewButtonClickListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
