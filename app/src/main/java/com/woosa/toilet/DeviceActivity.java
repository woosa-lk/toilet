package com.woosa.toilet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceActivity extends AppCompatActivity implements IGetMessageCallBack{
    List<DeviceData> list_dev = new ArrayList<>();
    private DeviceAdapter adapter;

    private DeviceActivity.MyServiceConnection serviceConnection;
    private IBinder binder;

    private final static int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTitle(R.string.activity_dev);

        //mqtt service
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(DeviceActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        ListView listView = findViewById(R.id.device_list);
        adapter = new DeviceAdapter(list_dev, DeviceActivity.this);
        listView.setAdapter(adapter);
    }

    //get init data.
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
            Log.e("Device activity:","Service Connected.");
            binder = iBinder;
            mqttService = ((MQTTService.CustomBinder)iBinder).getService();
            mqttService.setIGetMessageCallBack(IGetMessageCallBack);
            get_system_bind_dev();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("Device activity:","Service Connected.");
        }

        public void setIGetMessageCallBack(IGetMessageCallBack IGetMessageCallBack){
            this.IGetMessageCallBack = IGetMessageCallBack;
        }
    }

    @Override
    public void setMessage(String message) throws JSONException {
        Log.e("Device Message recv:", message);
        JSONObject cmd = new JSONObject(message);
        switch (cmd.getString("cmd")) {
            case "CMD_RET_INIT_DATA":
                list_dev.clear();
                DeviceData item;
                JSONArray dev_id = cmd.getJSONArray("dev_id");
                for (int dev_num = 0; dev_num < dev_id.length(); dev_num++) {
                    item = new DeviceData((String) dev_id.get(dev_num), R.drawable.bind);
                    list_dev.add(item);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Device :","onResume");
        if (serviceConnection !=null)
        {
            unbindService(serviceConnection);
        }
        serviceConnection = new DeviceActivity.MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(DeviceActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Device message", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Device message", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Device message", "onDestroy");
        unbindService(serviceConnection);
    }
}
