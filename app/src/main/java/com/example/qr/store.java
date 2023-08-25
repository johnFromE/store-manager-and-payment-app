package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.annotations.Nullable;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileWriter;

public class store extends AppCompatActivity implements View.OnClickListener{
    Button scan_btn,store,view_btn;
    EditText item,name,price,o_price,r_amount;
    EditText item_id;
    DBhelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        //changeing navigathon bar color and status bar colors
        toggleColorOfStatusBarIcons(store.this);

        DB = new DBhelper(this);
        item_id = findViewById(R.id.textToView);

        scan_btn = findViewById(R.id.scan_two);
        scan_btn.setOnClickListener(this);

        item = findViewById(R.id.product_two);
        name = findViewById(R.id.name_two);
        price = findViewById(R.id.price_two);
        o_price = findViewById(R.id.o_price);
        r_amount = findViewById(R.id.amount_r);


        store = findViewById(R.id.store_two);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item_id.getText().toString().isEmpty()||item.getText().toString().isEmpty()||name.getText().toString().isEmpty()||price.getText().toString().isEmpty()||o_price.getText().toString().isEmpty()||r_amount.getText().toString().isEmpty()){
                    Toast.makeText(store.this, "Field Is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    boolean result = DB.insertData(item_id.getText().toString(),item.getText().toString(),name.getText().toString(),price.getText().toString(),o_price.getText().toString(),r_amount.getText().toString());
                    if(result){
                        Toast.makeText(store.this,"inserted!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(store.this,"not inserted!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        view_btn = findViewById(R.id.view_two);
        view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item_id.getText().toString().isEmpty()){
                     Toast.makeText(store.this, "Field Is Empty", Toast.LENGTH_SHORT).show();
                }else{
                    Cursor cursor = DB.view("id");
                    if (cursor.getCount()==0){
                        Toast.makeText(store.this,"No Data!",Toast.LENGTH_SHORT).show();
                    }else{
                        while(cursor.moveToNext()){
                            if(item_id.getText().toString().equals(cursor.getString(0))) {
                                item.setText(cursor.getString(1));
                                name.setText(cursor.getString(2));
                                price.setText(cursor.getString(3));
                                o_price.setText(cursor.getString(4));
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scanning...");
        integrator.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                item_id.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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