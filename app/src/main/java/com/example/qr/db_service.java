package com.example.qr;

import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.google.firebase.database.annotations.Nullable;


public class db_service extends Service {

    Boolean go=true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        print();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        go = false;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void print(){
        while(go){
            Toast.makeText(this, "hy jo", Toast.LENGTH_SHORT).show();
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

