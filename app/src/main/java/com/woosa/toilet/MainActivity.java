package com.woosa.toilet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack{
    private List<Map<String, Object>> list = new ArrayList<>();
    private MainAdapter adapter;

    private Intent serviceIntent;
    private MessageReceiver messageReceiver;
    private IntentFilter intentFilter;

    private MyServiceConnection serviceConnection;
    private IBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //view init
        ListView listView = findViewById(R.id.main_list);
        adapter = new MainAdapter(list, this);
        listView.setAdapter(adapter);

        //mqtt service
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        //broadcast init.
        serviceIntent = new Intent(MainActivity.this, MQTTService.class);
        messageReceiver = new MessageReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MQTTService.ACTION_INIT_DATA);
        registerReceiver(messageReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(messageReceiver);
    }

    //service binder
    private void get_system_device_status()
    {
        Parcel data=Parcel.obtain();
        data.writeString("CMD_GET_SYS_INIT_DATA");
        Parcel reply=Parcel.obtain();
        try {
            binder.transact(IBinder.LAST_CALL_TRANSACTION, data, reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void get_system_bind_dev()
    {
        Parcel data=Parcel.obtain();
        data.writeString("CMD_GET_INIT_DATA");
        Parcel reply=Parcel.obtain();
        try {
            binder.transact(IBinder.LAST_CALL_TRANSACTION, data, reply, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        private MQTTService mqttService;
        private IGetMessageCallBack IGetMessageCallBack;

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = iBinder;
            mqttService = ((MQTTService.CustomBinder)iBinder).getService();
            mqttService.setIGetMessageCallBack(IGetMessageCallBack);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack){
            this.IGetMessageCallBack = IGetMessageCallBack;
        }
    }

    public List<Map<String,Object>> getDeviceStatusData(String message) throws JSONException {
        Map map;
        JSONObject root = new JSONObject(message);
        JSONArray data = root.getJSONArray("data");

        for (int num = 0; num < data.length(); num++)
        {
            JSONObject msg = data.getJSONObject(num);
            map = new HashMap<String,Object>();
            map.put("title", msg.getString("floor"));
            map.put("man", "男");
            map.put("woman", "女");
            map.put("busy", "占用坑位");
            map.put("free", "剩余坑位");
            map.put("busy_man", msg.getString("busy_man"));
            map.put("free_man", msg.getString("free_man"));
            map.put("busy_woman", msg.getString("busy_woman"));
            map.put("free_woman", msg.getString("free_woman"));
            map.put("img", R.drawable.toilet);
            if (msg.getString("free_man").equals("0")){
                map.put("img_man_status", R.drawable.busy);
            } else {
                map.put("img_man_status", R.drawable.idle);
            }
            if (msg.getString("free_woman").equals("0")){
                map.put("img_woman_status", R.drawable.busy);
            } else {
                map.put("img_woman_status", R.drawable.idle);
            }
            list.add(map);
        }

        return list;
    }

    @Override
    public void setMessage(String message) throws JSONException {
        Log.e("Main Message recv:", message);
        JSONObject cmd = new JSONObject(message);
        switch (cmd.getString("cmd")) {
            case "MQTT_CONNECT_SUCCESS":
                get_system_device_status();
                get_system_bind_dev();
                break;
            case "CMD_RET_INIT_DATA":
                /*
                myBeanList.clear();
                JSONArray dev_id = cmd.getJSONArray("dev_id");
                ListView listView = findViewById(R.id.listview);
                for (int dev_num = 0; dev_num < dev_id.length(); dev_num++) {
                    myBean bean = new myBean((String) dev_id.get(dev_num), R.drawable.bind);
                    myBeanList.add(bean);
                }
                adapter = new viewAdapter(MainActivity.this, R.layout.view_adapter, myBeanList);
                listView.setAdapter(adapter);
                */
                break;
            case "CMD_RET_SYS_INIT_DATA":
                //get list data view
                list.clear();
                list = getDeviceStatusData(message);
                //update view
                adapter.notifyDataSetChanged();
                break;
        }
    }
    //broadcast
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String str_msg = serviceIntent.getStringExtra(MQTTService.SERVER_DATA);

        }
    }

    //menu ----------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.bind_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scan_dev:
                Intent intent_list=new Intent();
                intent_list.setClass(MainActivity.this, DeviceActivity.class);
                //intent_list.putExtra("Context", (Parcelable) MainActivity.this);
                startActivity(intent_list);
                get_system_bind_dev();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
