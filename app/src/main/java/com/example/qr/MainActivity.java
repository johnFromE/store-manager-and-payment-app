package com.example.qr;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// implements onClickListener for the onclick behaviour of button
public class MainActivity extends AppCompatActivity {

    DBhelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.little_white));
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.little_white));

        DB = new DBhelper(this);

        //must be removed latter don't forget-------------------------
//        Intent intent_o = new Intent(MainActivity.this, HomeActivity.class);
//        startActivity(intent_o);
//        finish();
        //------------------------------------------------------------

        Cursor cursor = DB.viewUser();
        if (cursor.getCount()==0){
            Intent intent = new Intent(MainActivity.this,login.class);//to login was
            startActivity(intent);
            finish();
        }else{
            Cursor cursor_2 = DB.viewPin();
            if(cursor_2.getCount()==0){
                String user_type="";
                while(cursor.moveToNext()){
                    user_type = cursor.getString(2);
                }
                if(user_type.equals("Saler")) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);//to homeactivity was
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(MainActivity.this, BuyerHome.class);//to homeactivity was
                    startActivity(intent);
                    finish();
                }
            }
            else{
                Intent intent = new Intent(MainActivity.this,PIN_layout.class);
                startActivity(intent);
                finish();
            }

        }



    }
}