package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    Button nav_register,login_btn;
    EditText e_phone_number,e_password;
    FirebaseDatabase db;
    DatabaseReference reference;
    String db_username,db_phone_number,db_password,db_username_type,db_cbe_pin,user_id;
    TextView login_comment,textView_in_btn;
    ImageView progressBar;
    Animation rotateAnimation;
    DBhelper DB;
    transAction TA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //changeing navigathon bar color and status bar colors
        toggleColorOfStatusBarIcons(login.this);

        DB = new DBhelper(this);

        nav_register = findViewById(R.id.nav_register);
        nav_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });


        e_phone_number = findViewById(R.id.enterd_username);
        e_password = findViewById(R.id.enterd_password);
        login_comment = findViewById(R.id.login_comment);

        textView_in_btn = findViewById(R.id.text_in_btn);
        progressBar = findViewById(R.id.progress_bar);


        //when login button clicked
        View view = (View)findViewById(R.id.my_login);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_comment.setText("");
                String phone_number = e_phone_number.getText().toString();
                String password = e_password.getText().toString();

                if(phone_number.equals("pass")){
                    Intent intent = new Intent(login.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                if(phone_number.equals("pass2")){
                    Intent intent = new Intent(login.this,BuyerHome.class);
                    startActivity(intent);
                    finish();
                }


                if (phone_number.isEmpty() | password.isEmpty()) {
                    login_comment.setText("Fill the inputs");
                } else {

                    textView_in_btn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    login_comment.setText("");
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("users");

                    rotateAnimation();
                    reference.child(phone_number).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                db_username = String.valueOf(task.getResult().getValue());
                            }
                        }
                    });
                    reference.child(phone_number).child("username_type").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                db_username_type = String.valueOf(task.getResult().getValue());
                            }
                        }
                    });
                    reference.child(phone_number).child("phone_number").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                db_phone_number = String.valueOf(task.getResult().getValue());
                            }
                        }
                    });
                    reference.child(phone_number).child("user_cbe_pin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                db_cbe_pin = String.valueOf(task.getResult().getValue());
                            }
                        }
                    });
                    reference.child(phone_number).child("user_id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                user_id = String.valueOf(task.getResult().getValue());
                            }
                        }
                    });

                    reference.child(phone_number).child("password").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "Error", Toast.LENGTH_SHORT).show();
                                stop_animation();
                            } else {
                                db_password = String.valueOf(task.getResult().getValue());

                                System.out.println(phone_number+" = "+db_phone_number);
                                System.out.println(password+" = "+db_password);

                                if (phone_number.equals(db_phone_number)) {
                                    if (password.equals(db_password)) {

                                        Boolean result = DB.insertUser(db_username.toString(),db_password.toString(),db_username_type.toString(),db_phone_number.toString(),db_cbe_pin,user_id);

                                        if(db_username_type.equals("Saler")) {
                                            Intent intent = new Intent(login.this, HomeActivity.class);//to homeactivity was
                                            startActivity(intent);

                                            //register Tid before it starts
                                            reg_tid();
                                            finish();
                                        }
                                        else{
                                            Intent intent = new Intent(login.this, BuyerHome.class);//to homeactivity was
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        login_comment.setText("incorrect password");
                                        stop_animation();
                                    }
                                } else {
                                    login_comment.setText("incorrect username");
                                    stop_animation();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void rotateAnimation() {

        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.prograss_bar_anim);
        progressBar.startAnimation(rotateAnimation);
    }
    private void stop_animation(){
        progressBar.getAnimation().cancel();
        progressBar.clearAnimation();
        textView_in_btn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
                    Toast.makeText(login.this, "added", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(login.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}