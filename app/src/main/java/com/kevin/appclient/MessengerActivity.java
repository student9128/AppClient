package com.kevin.appclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

/**
 * Created by Kevin on 2019/4/9<br/>
 * Blog:https://blog.csdn.net/student9128<br/>
 * Describe:<br/>
 */
public class MessengerActivity extends AppCompatActivity {
    private ServiceConnection mSC;
    private Messenger mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.kevin.serverclient", "com.kevin.serverclient.MessengerService"));
        intent.setAction("com.kevin.messenger");
        mSC = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = new Messenger(service);
                Message msg = Message.obtain(null, 1001);
                Bundle bundle = new Bundle();
                bundle.putString("msg", "Hello, this msg is from client");
                msg.setData(bundle);
                msg.replyTo = m;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent, mSC, Context.BIND_AUTO_CREATE);

    }

    private static class ReplyMessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1002:
                    String s = msg.getData().getString("replyTo");
                    Log.d("MessengerActivity", "收到来自服务器回复：" + s);
                    break;
            }
        }
    }

    private Messenger m = new Messenger(new ReplyMessengerHandler());

    @Override
    protected void onDestroy() {
        unbindService(mSC);
        super.onDestroy();
    }
}
