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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class BindDevActivity extends AppCompatActivity implements IGetMessageCallBack{
    private Button btn_bind;
    private TextView text_devid;
    private Spinner sp_floor;
    private Spinner sp_sex;
    private int pos_id;
    public final static int RESULT_CODE = 102;

    private BindDevActivity.MyServiceConnection serviceConnection;
    private IBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_dev);
        setTitle("绑定设备");

        //mqtt service
        serviceConnection = new BindDevActivity.MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(BindDevActivity.this);
        final Intent intent = new Intent(this, MQTTService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        final TextView dev_id = findViewById(R.id.text_dev_id);
        dev_id.setText(getIntent().getExtras().getString("dev_id"));
        pos_id = getIntent().getExtras().getInt("pos_id");

        btn_bind = findViewById(R.id.view_btn_bind);
        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_devdata_to_service();

                Intent intent_list = new Intent();
                intent_list.setClass(BindDevActivity.this, DeviceActivity.class);
                finish();
                Toast.makeText(getApplicationContext(), "成功绑定设备" + text_devid.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("BindDev message", "onDestroy");
        unbindService(serviceConnection);
    }

    private void send_devdata_to_service()
    {
        text_devid = findViewById(R.id.text_dev_id);
        sp_floor = findViewById(R.id.spinner_floor);
        sp_sex = findViewById(R.id.spinner_sex);

        Parcel data_bind=Parcel.obtain();
        data_bind.writeString("CMD_SET_BIND_DEV");
        data_bind.writeString((String) text_devid.getText());
        data_bind.writeString(sp_floor.getSelectedItem().toString());
        data_bind.writeString(sp_sex.getSelectedItem().toString());
        Parcel reply=Parcel.obtain();
        try {
            binder.transact(IBinder.LAST_CALL_TRANSACTION, data_bind, reply, 0);
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
        Log.e("Main Message recv:", message);
        JSONObject cmd = new JSONObject(message);
        switch (cmd.getString("cmd")) {
            case "MQTT_CONNECT_SUCCESS":

                //get_system_bind_dev();
                break;
            case "CMD_RET_SYS_INIT_DATA":

                break;
        }
    }
}
