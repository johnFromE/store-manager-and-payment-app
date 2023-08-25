package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;


public class Register extends AppCompatActivity {


    transAction TA;
    Spinner spinner;
    Button register_btn;
    ImageView back_btn;
    EditText username,password,cbe_pin,confirm_password;
    EditText phone_number;
    DBhelper DB;
    String comments = "";
    TextView comments_text,textView_in_btn;
    String firstName,lastName, age ,userName;
    FirebaseDatabase db;
    DatabaseReference reference;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    String user_phone_num="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //changeing navigathon bar color and status bar colors
        toggleColorOfStatusBarIcons(Register.this);


        DB = new DBhelper(this);


        spinner = findViewById(R.id.spinner_one);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        phone_number = findViewById(R.id.phone_number);
        cbe_pin = findViewById(R.id.cbe_pin);
        comments_text = findViewById(R.id.comment_text);



        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //form validation
                Boolean pass = true;
                register_btn.setText("Loading...");

                String username_un = username.getText().toString();
                String password_un = password.getText().toString();
                String confirm_password_un = confirm_password.getText().toString();
                String phone_number_un = phone_number.getText().toString();
                String cbe_pin_un = cbe_pin.getText().toString();

                if(username_un.matches(".*[0-9].*")){
                       comments = "username can't include numbers";
                       pass = false;
                }
                if(pass == true){
                    if(username_un.isEmpty() | phone_number_un.isEmpty() | cbe_pin_un.isEmpty()){
                        comments = "fill all inputs";
                        pass = false;
                    }
                }
                if(pass == true) {
                    if (username_un.matches(".*[/.!@#$%&*()_+=|<>?{}\\[\\]~-].*")) {
                        comments = "username can't include symbols";
                        pass = false;
                    }
                }
                if(pass == true) {
                    if (password_un.matches(".*[ /.!@#$%&*()_+=|<>?{}\\[\\]~-].*")) {
                        comments =  "password can't include symbols or spaces";
                        pass = false;
                    }
                }
                if(pass == true) {
                    if(password_un.length() < 6){
                        comments =  "password must be grater than six digits less then eight digits";
                        pass = false;
                    }
                }
                if(pass == true) {
                    if(password_un.length() > 8){
                        comments =  "password must be grater than six digits less then eight digits";
                        pass = false;
                    }
                }
                if(pass == true) {
                    if(confirm_password_un.equals(password_un)){
                        pass = true;
                    }
                    else{
                        comments =  "passwords don't match";
                        pass = false;
                    }
                }
                if(pass == true){
                    if(9 != phone_number_un.length() | cbe_pin_un.matches(".*[a-zA-Z].*") | cbe_pin_un.matches(".*[ /.!@#$%&*()_+=|<>?{}\\[\\]~-].*")){
                        comments = "phone number incorrect";
                        pass= false;
                    }
                }
                if(pass == true){
                    if(phone_number_un.startsWith("9")){
                        pass = true;
                    }
                    else{
                        comments = "phone number incorrect";
                        pass = false;
                    }
                }
                if(pass == true){
                    if(cbe_pin_un.matches(".*[a-zA-Z].*")|cbe_pin_un.matches(".*[ /.!@#$%&*()_+=|<>?{}\\[\\]~-].*")){
                        comments = "CBE PIN must only contain numbers";
                        pass = false;
                    }
                }
                if(pass == true){
                    if(cbe_pin_un.length()!=4){
                        comments = "CBE PIN must be four digits";
                        pass = false;
                    }
                }



                if(pass == true ) {
                    comments_text.setText("");

                    String user_username = username.getText().toString();
                    String user_password = password.getText().toString();
                    String user_phone = phone_number.getText().toString();
                    String user_type = spinner.getSelectedItem().toString();
                    String user_cbe_pin = cbe_pin.getText().toString();

                    if (!username.getText().toString().isEmpty()){
                        //register on firebase

                        UUID uuid = UUID.randomUUID();
                        String user_id = uuid.toString();

                        HelperClass users = new HelperClass(user_username,user_password,"0"+user_phone,user_type,user_cbe_pin,user_id);
                        db = FirebaseDatabase.getInstance();
                        if(user_type.equals("Saler")) {
                            reference = db.getReference("users/saler");
                        }else{
                            reference = db.getReference("users/buyer");
                        }
                        reference.child(user_id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    register_btn.setText("REGISTER");
                                    Toast.makeText(Register.this, "Not Registered", Toast.LENGTH_SHORT).show();
                                    Exception e = task.getException();
                                    if (e != null) {
                                        Log.e("TAG", "Error: " + e.getMessage());
                                    }
                                }else{
                                    Toast.makeText(Register.this, "Registered", Toast.LENGTH_SHORT).show();
                                    //register on local database
                                    Boolean result = DB.insertUser(username.getText().toString(), password.getText().toString(), user_type,"0"+phone_number.getText().toString(), cbe_pin.getText().toString(),user_id);

                                    if(user_type.equals("Saler")) {
                                        Intent intent = new Intent(Register.this, HomeActivity.class);//to homeactivity was
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Intent intent = new Intent(Register.this, BuyerHome.class);//to homeactivity was
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                }
                else{
                    register_btn.setText("REGISTER");
                    comments_text.setText(comments);
                }
            }
        });

        // Check if the SMS permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)==0) {
            // Permission is already granted, proceed with your app logic
            getPhoneNumber();
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PHONE}, SMS_PERMISSION_REQUEST_CODE);
        }
        //end of main --------------------------------
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults[1] == 0) {
                getPhoneNumber();
            } else {
                Toast.makeText(this, "Phone CALL permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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

    public void reg_tid(){

        //get new Tid----------------------------
        TA = new transAction();
        String user_phone = "";
        Cursor cursor = DB.viewUser();
        if(cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                user_phone = cursor.getString(3);
            }
        }
        String Tid = TA.getTid(user_phone);
        //----------------------------------------

        HelperCalss2 tranAction = new HelperCalss2(Tid,"null","null","null","null","null","null","null");
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("transaction");

        Boolean result = DB.insertTid(Tid);

        //passing to firbase
        reference.child(Tid).setValue(tranAction).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "added", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Register.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getPhoneNumber(){

        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        manager.sendUssdRequest("*111#", new TelephonyManager.UssdResponseCallback() {
            @Override
            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                super.onReceiveUssdResponse(telephonyManager, request, response);

                String u_phone_num = String.valueOf(response.toString());
                u_phone_num = u_phone_num.replace("MSISDN: 251","");
                phone_number.setText(u_phone_num);
            }

            @Override
            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);

            }
        }, new Handler());
        return " ";
    }
}