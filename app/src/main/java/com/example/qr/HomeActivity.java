package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPageAdapter viewPageAdapter;
    BottomNavigationView bottomNavigationView;
    ImageButton settingbtn;
    LinearLayout layout;
    private NetworkChangeReceiver networkChangeReceiver;
    FirebaseDatabase db;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //changeing navigathon bar color and status bar colors
        toggleColorOfStatusBarIcons(HomeActivity.this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.viewPager);
        viewPageAdapter = new ViewPageAdapter(this);
        viewPager2.setAdapter(viewPageAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.dashboardFragment):
                        viewPager2.setCurrentItem(0);
                        break;
                    case (R.id.homeFragment):
                        viewPager2.setCurrentItem(1);
                        break;
                    case (R.id.analysis):
                        viewPager2.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.dashboardFragment).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.homeFragment).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.analysis).setChecked(true);
                }
                super.onPageSelected(position);
            }
        });
        viewPager2.setCurrentItem(1);
        bottomNavigationView.getMenu().findItem(R.id.homeFragment).setChecked(true);

        //go to setting layout on setting button clicked


        layout =findViewById(R.id.internet_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setBackgroundColor(ContextCompat.getColor(HomeActivity.this,R.color.green));
                getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.green));
                TextView i_status = findViewById(R.id.internet_status);
                i_status.setText("Internet Connceted");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
                    }
                },500);
            }
        });



        // Create an instance of the network change receiver
        networkChangeReceiver = new NetworkChangeReceiver();

        // Register the receiver to listen for network connectivity changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);



        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                if (950 > screenHeight) {
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });
        if (isNetworkAvailable()) {
            //internet available
        } else {
            //internet not available
        }

        //update info to cloude
        upload_to_firebase();

        //---------------------------------------------------------------------
        //end of main method --------------------------------------------------
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to prevent leaks
        unregisterReceiver(networkChangeReceiver);
    }
    public void toggleColorOfStatusBarIcons(Activity activity){
        if(Build.VERSION.SDK_INT >= 23){
            View decor = activity.getWindow().getDecorView();
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.little_white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

            if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }
    }
    public void upload_to_firebase(){
            DBhelper DB = new DBhelper(this);
            Cursor cursor_1 = DB.viewUser();
            String saler_id="";
            while(cursor_1.moveToNext()){
                saler_id = cursor_1.getString(5).toString();
            }
            
            
            Cursor cursor = DB.viewMonth();
            while (cursor.moveToNext()) {



                String route_reference = saler_id +"/" + cursor.getString(4) + "/" + cursor.getString(5) + "/" + cursor.getString(6) +"/"+ cursor.getString(0);
                System.out.println(route_reference);
                float tax = (Float.parseFloat(cursor.getString(3)) * 15) / 100;

                db = FirebaseDatabase.getInstance();
                reference = db.getReference("tax_info");
                reference.child(route_reference).child("amount").setValue(cursor.getString(2));
                reference.child(route_reference).child("item").setValue(cursor.getString(1));
                reference.child(route_reference).child("tax").setValue(tax+"");
                reference.child(route_reference).child("T-ID").setValue(cursor.getString(0))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}