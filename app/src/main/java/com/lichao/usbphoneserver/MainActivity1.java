package com.lichao.usbphoneserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity1 extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "lichao";

    private SocketServerThread socketServerThread;
    private TextView textView;
    private EditText et_msg;
    private Button bt_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        socketServerThread = new SocketServerThread();
        socketServerThread.start();

        initView();
    }

    private void initView() {
        textView = findViewById(R.id.text);
        et_msg = findViewById(R.id.et_msg);
        bt_send = findViewById(R.id.bt_send);

        et_msg.setText("123");
        bt_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                socketServerThread.SendMsg(et_msg.getText().toString());
                Log.i(TAG, "send===" + et_msg.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
