package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PIN_layout extends AppCompatActivity {

    DBhelper DB;
    EditText login_pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_layout);
        toggleColorOfStatusBarIcons(PIN_layout.this);

        DB = new DBhelper(this);
        login_pin = findViewById(R.id.login_pin);
        Button one_btn = findViewById(R.id.one_btn);
        Button two_btn = findViewById(R.id.two_btn);
        Button three_btn = findViewById(R.id.three_btn);
        Button four_btn = findViewById(R.id.four_btn);
        Button five_btn = findViewById(R.id.five_btn);
        Button six_btn = findViewById(R.id.six_btn);
        Button seven_btn = findViewById(R.id.seven_btn);
        Button eight_btn = findViewById(R.id.eight_btn);
        Button nigen_btn = findViewById(R.id.nigen_btn);
        Button zero_btn = findViewById(R.id.zero_btn);
        ImageButton clear_btn = findViewById(R.id.clear_btn);

        one_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("1");
            }
        });
        two_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("2");
            }
        });
        three_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("3");
            }
        });
        four_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("4");
            }
        });
        five_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("5");
            }
        });
        six_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("6");
            }
        });
        seven_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("7");
            }
        });
        eight_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("8");
            }
        });
        nigen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("9");
            }
        });
        zero_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("0");
            }
        });
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_pin.setText("");
            }
        });

    }
    private void add(String num){
        String temp_value = login_pin.getText().toString();
        String login_value = temp_value + num;
        login_pin.setText(login_value);

        if(login_pin.getText().toString().length()==4){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    login();
                }
            },200);
        }
    }

    private void login(){
        String pin="";
        String l_pin = login_pin.getText().toString();
        Cursor cursor = DB.viewPin();
        if (cursor.getCount()==0){
            Toast.makeText(PIN_layout.this,"Not Registered!",Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                pin = cursor.getString(0);
            }
            if(l_pin.equals(pin)){

                Cursor cursor_user = DB.viewUser();
                String user_type="";
                if(cursor_user.getCount()!=0) {
                    System.out.println();
                    while (cursor_user.moveToNext()) {
                        user_type = cursor_user.getString(2);
                    }
                    if (user_type.equals("Saler")) {
                        Intent intent = new Intent(PIN_layout.this, HomeActivity.class);//to homeactivity was
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PIN_layout.this, BuyerHome.class);//to buyer homeactivity
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    System.out.println("user not registered");
                }
            }
            else{
                Toast.makeText(PIN_layout.this,"PIN incorrect!",Toast.LENGTH_SHORT).show();
                login_pin.setText("");
            }
        }
    }
    public void toggleColorOfStatusBarIcons(Activity activity){
        if(Build.VERSION.SDK_INT >= 23){
            View decor = activity.getWindow().getDecorView();
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

            if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }
    }
}