package com.woosa.toilet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class DeviceAdapter extends BaseAdapter{
    private final static int REQUEST_CODE = 101;
    private LayoutInflater mInflater;
    private List<DeviceData> item_list = new ArrayList<>();;
    public Context activity;

    public DeviceAdapter(List<DeviceData> list, Context context){
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
        public ImageView fruitImage;
        public TextView fruitDevID;
        public LinearLayout fruitLayout;
        public Button fruitBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //DeviceData device_data = (DeviceData) getItem(position);
        DeviceAdapter.ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_adapter, null);

            holder=new ViewHolder();
            holder.fruitImage = convertView.findViewById(R.id.img_dev);
            holder.fruitDevID = convertView.findViewById(R.id.text_dev_id);
            holder.fruitLayout = convertView.findViewById(R.id.ll_view);
            holder.fruitBtn = convertView.findViewById(R.id.view_btn_bind);

            convertView.setTag(holder);
        } else {
            holder = (DeviceAdapter.ViewHolder)convertView.getTag();
        }

        holder.fruitDevID.setText(item_list.get(position).getText());
        holder.fruitLayout.setTag(position);

        holder.fruitBtn.setOnClickListener(new viewButtonClickListener(position));

        return convertView;
    }

    private class viewButtonClickListener implements View.OnClickListener {
        int position;
        public viewButtonClickListener(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            item_list.get(position).toString();
            Intent intent_list=new Intent();
            intent_list.setClass(activity, BindDevActivity.class);
            intent_list.putExtra("dev_id", item_list.get(position).getText());
            intent_list.putExtra("pos_id", position);
            activity.startActivity(intent_list);
            //((Activity)activity.getApplicationContext()).startActivityForResult(intent_list, REQUEST_CODE);

            //remove item.
            //item_list.remove(position);
            //notifyDataSetChanged();
        }
    }
}
