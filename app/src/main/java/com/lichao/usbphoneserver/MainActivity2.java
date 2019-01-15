package com.lichao.usbphoneserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity2 extends AppCompatActivity {
    private final String TAG = MainActivity2.class.getName();

    private final int port = 18100;
    private ServerSocket serverSocket = null;
    private boolean isExitFlag = false;
    private Socket client = null;
    private InputStream in = null;
    private OutputStream outToPc = null;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = findViewById(R.id.text);

        startLinsten();
    }

    private void startLinsten(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "phone startListen");
                    if (serverSocket == null) {
                        serverSocket = new ServerSocket();
                        serverSocket.setReuseAddress(true);
                        serverSocket.bind(new InetSocketAddress(port));
                    }
                    //serverSocket = new ServerSocket(port);
                    client = serverSocket.accept();
                    Log.e(TAG, "有客户端连接");
                    startRecMsg(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startRecMsg(final Socket s){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String content = null;
                    in = s.getInputStream();
                    outToPc = s.getOutputStream();
                    BufferedReader inReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    while(!isExitFlag){
                        if((content = inReader.readLine()) != null){ //readline是阻塞的，当流不可读时一直处于等待，while之后的无法执
                            //行，导致结果是不知道数据是否已经接收完成和处理
                            Log.e(TAG, "收到消息:" + content);
                            //content = content + "\r\n";
                            final String msg = content;
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(msg);
                                }
                            });

                            if(outToPc != null){
                                outToPc.write(("手机端:我已接收到pc端的消息是(" + msg + ")\r\n").getBytes("utf-8"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isExitFlag = true;

        try {
            if (in != null) {
                in.close();
            }

            if (outToPc != null) {
                outToPc.close();
            }

            if (client != null) {
                client.close();
            }

            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
