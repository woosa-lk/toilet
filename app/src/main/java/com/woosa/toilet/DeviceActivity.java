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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceActivity extends AppCompatActivity implements IGetMessageCallBack{
    List<Map<String, Object>> list_dev = new ArrayList<>();
    private DeviceAdapter adapter;

    private DeviceActivity.MyServiceConnection serviceConnection;
    private IBinder binder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTitle(R.string.activity_dev);

        ListView listView = findViewById(R.id.device_list);
        adapter = new DeviceAdapter(list_dev, this);
        listView.setAdapter(adapter);

        //mqtt service
        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(DeviceActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

    @Override
    public void setMessage(String message) throws JSONException {
        Log.e("Device Message recv:", message);
        JSONObject cmd = new JSONObject(message);
        switch (cmd.getString("cmd")) {
            case "MQTT_CONNECT_SUCCESS":

                break;
            case "CMD_RET_INIT_DATA":
                list_dev.clear();
                Map map;
                JSONArray dev_id = cmd.getJSONArray("dev_id");
                for (int dev_num = 0; dev_num < dev_id.length(); dev_num++) {
                    //JSONObject msg = dev_id.getJSONObject(dev_num);
                    map = new HashMap<String,Object>();
                    map.put("text_dev_id", dev_id.get(dev_num));
                    list_dev.add(map);
                }
                adapter.notifyDataSetChanged();
                break;
            case "CMD_RET_SYS_INIT_DATA":
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Device message", "onPause");
    }

    /** 当活动不再可见时调用 */
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
