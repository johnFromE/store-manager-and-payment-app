package com.example.qr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        LinearLayout layout =((Activity)context).findViewById(R.id.internet_layout);
        TextView i_status = ((Activity)context).findViewById(R.id.internet_status);
        View decor = ((Activity)context).getWindow().getDecorView();


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // There is an active network connection
            layout.setBackgroundColor(ContextCompat.getColor(((Activity)context),R.color.green));
            ((Activity)context).getWindow().setStatusBarColor(ContextCompat.getColor(((Activity)context),R.color.green));
            i_status.setText("Internet Connected");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                    ((Activity)context).getWindow().setStatusBarColor(ContextCompat.getColor(((Activity)context),R.color.white));
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            },500);
        } else {
            // There is no active network connection
            layout.setBackgroundColor(ContextCompat.getColor(((Activity)context),R.color.dark_red));
            ((Activity)context).getWindow().setStatusBarColor(ContextCompat.getColor(((Activity)context),R.color.dark_red));
            i_status.setText("No Internet connection");
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30));

            decor.setSystemUiVisibility(0);
        }
    }
}
