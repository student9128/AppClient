package com.kevin.appclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.*;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "appTestClient";
    private ServiceConnection mSC;
    private DeathRecipient deathRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.kevin.serverclient", "com.kevin.serverclient.TestService"));
        intent.setAction("com.kevin.server");
        deathRecipient = new DeathRecipient() {

            @Override
            public void binderDied() {
                Log.d(TAG, "binder is died");
                Looper.prepare();
                Toast.makeText(MainActivity.this,"服务器关掉了",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };
        mSC = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected "+name.toShortString());
                try {
                    service.linkToDeath(deathRecipient,0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected "+name.toShortString());
            }
        };
        bindService(intent, mSC, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mSC);
    }
}
