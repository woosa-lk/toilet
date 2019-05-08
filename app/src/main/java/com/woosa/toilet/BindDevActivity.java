package com.woosa.toilet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class BindDevActivity extends AppCompatActivity {
    private Button btn_bind;
    private TextView text_devid;
    private Spinner sp_floor;
    private Spinner sp_sex;
    private int pos_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_dev);
        setTitle("绑定设备");

        final TextView dev_id = findViewById(R.id.text_dev_id);
        dev_id.setText(getIntent().getExtras().getString("dev_id"));
        pos_id = getIntent().getExtras().getInt("pos_id");

        btn_bind = findViewById(R.id.view_btn_bind);
        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_devid = findViewById(R.id.text_dev_id);
                sp_floor = findViewById(R.id.spinner_floor);
                sp_sex = findViewById(R.id.spinner_sex);
                finish();
            }
        });
    }
}
