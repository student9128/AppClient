package com.kevin.appclient;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 2019/4/10<br/>
 * Blog:https://blog.csdn.net/student9128<br/>
 * Describe:<br/>
 */
public class TCPClientActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MESSAGE_RECEIVE_NEW_MSG = 101;
    private static final int MESSAGE_SOCKET_CONNECTED = 102;
    private Button mSendButton, mOKButton;
    private TextView mMessageTextView;
    private EditText mMessageEditText;
    private EditText mIPEditText;
    private PrintWriter mPrintWriter;
    private Socket mClientSocket;
    private String TAG = getClass().getSimpleName();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG:
                    mMessageTextView.setText(mMessageTextView.getText() + (String) msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED:
                    mSendButton.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_client);
        mMessageTextView = findViewById(R.id.msg_text);
        mMessageEditText = findViewById(R.id.msg_edit);
        mSendButton = findViewById(R.id.btn_send);
        mSendButton.setOnClickListener(this);
        mIPEditText = findViewById(R.id.et_ip);
        mOKButton = findViewById(R.id.btn_ok);
        mOKButton.setOnClickListener(this);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.kevin.serverclient", "com.kevin.serverclient.TCPServerService"));
        intent.setAction("com.kevin.server.tcp");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

    }

    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mSendButton) {
            final String msg = mMessageEditText.getText().toString();
            if (!TextUtils.isEmpty(msg) && mPrintWriter != null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        mPrintWriter.println(msg);
                    }
                }.start();
                mMessageEditText.setText("");
                String time = formatDateTime(System.currentTimeMillis());
                String shownMsg = "self " + time + ":" + msg + "\n";
                mMessageTextView.setText(mMessageTextView.getText() + shownMsg);
            }
        }
        if (v == mOKButton) {
            final String trim = mIPEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(trim)) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        connectTCPServer(trim);
                    }
                }.start();
            }
        }
    }

    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    private void connectTCPServer(String ip) {
        Socket socket = null;
        while (socket == null) {
            try {
                socket = new Socket(ip, 8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.d(TAG, "connect server successfully");
            } catch (IOException e) {
                e.printStackTrace();
                SystemClock.sleep(1000);
                Log.d(TAG, "connect tcp server failed, retrying...");
            }
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!TCPClientActivity.this.isFinishing()) {
                String msg = br.readLine();
                Log.d(TAG, "receive: " + msg);
                if (msg != null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    String shownMsg = "server " + time + ":" + msg + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG, shownMsg).sendToTarget();
                }
            }
            mPrintWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
