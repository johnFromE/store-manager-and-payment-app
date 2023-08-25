package com.example.qr;

import static java.lang.Thread.sleep;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BuyerHome extends AppCompatActivity {

    String scanResult;
    DBhelper DB;
    FirebaseDatabase db;
    DatabaseReference reference;
    String Tid="",s_phone="",done="";
    String r_items="",r_price="",r_tax="";
    TextView item_tv,price_tv,tax_tv;
    private Handler handler;
    private Runnable runnable;

    EditText buyer_Tid,buyer_text,buyer_items,buyer_price,buyer_tax,total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_home);
        //change color of action bar and status bar
        toggleColorOfStatusBarIcons(BuyerHome.this);

        buyer_Tid = findViewById(R.id.buyer_Tid);
        buyer_text = findViewById(R.id.buyer_text);
        buyer_items = findViewById(R.id.buyer_items);
        buyer_price = findViewById(R.id.buyer_price);
        buyer_tax = findViewById(R.id.buyer_tax);
        total_price = findViewById(R.id.total_price);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },4000);


        DB = new DBhelper(this);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("transaction");

        findViewById(R.id.buyer_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
        //pay button on click
        findViewById(R.id.pay_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upadet_db();
                //dialNumber("*804#");
                String price = total_price.getText().toString();
                price = price.replace(".","-");

                for(String p : price.split("-")){
                    price = p;
                    break;
                }
                if(buyer_text.getText().toString().isEmpty()){

                }else {
                    String cbe_pin = "";
                    Cursor cursor = DB.viewUser();
                    if(cursor.getCount()!=0) {
                        while (cursor.moveToNext()) {
                            cbe_pin = cursor.getString(4);
                        }
                    }
                    dialNumber("*847#1*1*" + buyer_text.getText().toString() + "*" + price + "*" + cbe_pin + "*1#");
                }
            }
        });
        //go to setting layout on setting button clicked
        findViewById(R.id.buyer_settingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerHome.this,buyer_setting.class);
                startActivity(intent);
                finish();
            }
        });

        //end of main method ----------------------
        //-----------------------------------------
    }
    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scanning...");
        integrator.initiateScan();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,BuyerHome.class);
                startActivity(intent);
                finish();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                divide(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void upadet_db(){
        Cursor cursor = DB.viewUser();
        if(cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                s_phone = cursor.getString(3);
            }
        }
        reference.child(Tid).child("sender_p").setValue(s_phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(BuyerHome.this, "success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BuyerHome.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getRecite(){
        reference.child(Tid).child("items").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                r_items = String.valueOf(task.getResult().getValue());
            }
        });
        reference.child(Tid).child("price").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                r_price = String.valueOf(task.getResult().getValue());
            }
        });
        reference.child(Tid).child("tax").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                r_tax = String.valueOf(task.getResult().getValue());
            }
        });
    }
    public void toggleColorOfStatusBarIcons(Activity activity){
        if(Build.VERSION.SDK_INT >= 23){
            View decor = activity.getWindow().getDecorView();
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_blue));

            if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }
    }
    public String divide(String text) {

        String s_phone,  tax, items, item_Price;
        String text_arr[] = text.split("><");

        s_phone = text_arr[0];
        Tid = text_arr[1];
        items = text_arr[2];
        item_Price = text_arr[3];
        tax = text_arr[4];


        //get total value
//        float total_p = Float.parseFloat(tax);
//        for (String ip : item_Price.split("-")){
//            total_p = total_p + Float.parseFloat(ip);
//        }

        items = items.replace("--",",");
        item_Price = item_Price.replace("-","+");

        buyer_Tid.setText(Tid);
        buyer_text.setText(s_phone);
        buyer_items.setText(items);
        buyer_price.setText(item_Price);
        buyer_tax.setText(tax);
        total_price.setText(item_Price);

        return "";
    }

    private void dialNumber(String number) {


        View view = LayoutInflater.from(this).inflate(R.layout.ussd_dialog,null);
        ProgressBar progressBar = view.findViewById(R.id.alert_prograss_bar);
        TextView alert_text = view.findViewById(R.id.alert_text);
        ImageView done_img = view.findViewById(R.id.done_img);
        ImageButton close_pay = view.findViewById(R.id.close_payment);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.show();

        close_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.sendUssdRequest(number, new TelephonyManager.UssdResponseCallback() {
            @Override
            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                super.onReceiveUssdResponse(telephonyManager, request, response);

                if("Please try again".equals(response.toString())){
                    progressBar.setVisibility(View.GONE);
                    alert_text.setText("PLEASE TRY AGAIN");
                }else if("Your request is being processed. Please wait for CBE Birr confirmation message.".equals(response.toString())) {
                    alert_text.setText("PAYMENT SENT\n WAIT FOR CONFIRMATION");
                    progressBar.setVisibility(View.GONE);
                    done_img.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                alert_text.setText("FAILED");
                progressBar.setVisibility(View.GONE);
            }
        }, new Handler());

    }



}