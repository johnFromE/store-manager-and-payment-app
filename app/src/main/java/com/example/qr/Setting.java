package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    DBhelper DB;
    TextView logout_text;
    EditText pin_ed;
    ImageButton setting_backbtn;
    Dialog dialog,dialog_2;
    Switch aSwitch,tax_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //changeing navigathon bar color and status bar colors
        toggleColorOfStatusBarIcons(Setting.this);

        //check and enable switch
        enable_switch();
        enable_tax_switch();

        DB = new DBhelper(this);

        logout_text = findViewById(R.id.logout_text);
        logout_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB.Delete_User();
                Intent intent = new Intent(Setting.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        setting_backbtn = findViewById(R.id.setting_backbtn);
        setting_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        aSwitch = findViewById(R.id.switch_btn);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(aSwitch.isChecked()){
                    dialog.show();
                }else{
                    DB.Delete_pin();
                    Toast.makeText(Setting.this, "PIN disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });




        //-----------------------------------------
        //dialog box ------------------------------
        dialog = new Dialog(Setting.this);
        dialog.setContentView(R.layout.set_pin_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;

        Button save = dialog.findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin_ed = dialog.findViewById(R.id.pin_ed);
                String pin_value = pin_ed.getText().toString();
                if(pin_value.isEmpty()){
                    Toast.makeText(Setting.this, "Enter PIN", Toast.LENGTH_SHORT).show();
                }
                else if(pin_value.length()!=4){
                    Toast.makeText(Setting.this, "PIN must be four digits", Toast.LENGTH_LONG).show();
                }
                else{
                    DB.insertPin(pin_ed.getText().toString());
                    dialog.dismiss();
                    Toast.makeText(Setting.this, "PIN saved", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button cancel = dialog.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                aSwitch.setChecked(false);
            }
        });

        //--------------------------------------------
        //second dilog--------------------------------
        dialog_2 = new Dialog(Setting.this);
        dialog_2.setContentView(R.layout.set_pin_dialog);
        dialog_2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box));
        dialog_2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_2.setCancelable(false);
        dialog_2.getWindow().getAttributes().windowAnimations = R.style.my_animation;

        tax_switch = findViewById(R.id.enable_tax);
        tax_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(tax_switch.isChecked()){
                    dialog_2.show();
                    TextView dialog_text = dialog_2.findViewById(R.id.dialog_text);
                    dialog_text.setText("Enter Tax");
                }else{
                    DB.delete_tax();
                    Toast.makeText(Setting.this, "tax disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button save_2 = dialog_2.findViewById(R.id.save_btn);
        save_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pin_ed = dialog_2.findViewById(R.id.pin_ed);
                String pin_value = pin_ed.getText().toString();
                if(pin_value.isEmpty()){
                    Toast.makeText(Setting.this, "Enter tax", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(pin_value)>50){
                    Toast.makeText(Setting.this, "tax must be under 50%", Toast.LENGTH_LONG).show();
                }
                else if(Integer.parseInt(pin_value)<0){
                    Toast.makeText(Setting.this, "tax must be above 0%", Toast.LENGTH_LONG).show();
                }
                else{
                    DB.add_tax(pin_ed.getText().toString());
                    dialog_2.dismiss();
                    Toast.makeText(Setting.this, "tax saved", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button cancel_2 = dialog_2.findViewById(R.id.cancel_btn);
        cancel_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_2.dismiss();
                aSwitch.setChecked(false);
            }
        });


        findViewById(R.id.about_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Setting.this,about.class));
            }
        });


        //------------------------------------------------------
        //end of main method -----------------------------------
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
    public void enable_switch(){

        DB = new DBhelper(this);
        aSwitch = findViewById(R.id.switch_btn);
        Cursor cursor_2 = DB.viewPin();
        if(cursor_2.getCount()==0){
            aSwitch.setChecked(false);
        }
        else{
            aSwitch.setChecked(true);
        }
    }
    public void enable_tax_switch(){

        DB = new DBhelper(this);
        tax_switch = findViewById(R.id.enable_tax);
        Cursor cursor_2 = DB.view_tax("");
        if(cursor_2.getCount()==0){
            tax_switch.setChecked(false);
        }
        else{
            tax_switch.setChecked(true);
        }
    }
}