package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class item_list extends AppCompatActivity {

    private ListView myListView;
    ArrayAdapter<String> myAdapter;
    EditText inputSearch;
    ArrayList<HashMap<String, String>> productList;
    Button delete_btn;
    LinearLayout layout;
    DBhelper DB;
    String str_arry[];
    Boolean is_visable = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        DB = new DBhelper(this);
        String sq_value = "";

        Cursor cursor = DB.view("id");
        if (cursor.getCount()!=0){
            while(cursor.moveToNext()){
                if (sq_value.isEmpty()) {
                    sq_value = cursor.getString(2).toString();
                } else {
                    sq_value = sq_value + "-" + cursor.getString(2).toString();
                }
            }
        }

        str_arry = sq_value.toLowerCase().split("-");
        Arrays.sort(str_arry);

        layout = findViewById(R.id.container);

        inputSearch = (EditText) findViewById(R.id.itemSearch);

        for (String sr : str_arry) {
            addCard(sr);
        }


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layout.removeAllViews();
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //end of main-----------------------------------------
        //----------------------------------------------------
    }
    private void addCard(String name) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(20,0,0,0);

        RelativeLayout.LayoutParams params_2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_2.setMargins(10,10,10,0);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setPaddingRelative(10,10,10,10);
        relativeLayout.setBackgroundColor(Color.parseColor("#E8E8E8"));
        relativeLayout.setLayoutParams(params_2);


        TextView textView = new TextView(this);
        textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(name);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#2B2B2B"));
        relativeLayout.addView(textView);

        delete_btn = new Button(this);
        delete_btn.setLayoutParams(params);
        delete_btn.setText("delete");
        relativeLayout.addView(delete_btn);


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                DB.deleteItem(item);
                refreshView();
                Toast.makeText(item_list.this, item+ " :( deleted", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new Dialog(item_list.this);
        dialog.setContentView(R.layout.view_item);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = textView.getText().toString();
                //EditText id = dialog.findViewById(R.id.item_name_ed);
                //id.setText(item);
                //dialog.show();

            }
        });
        dialog.findViewById(R.id.close_btn_ed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        layout.addView(relativeLayout);
    }
    public String filter(String cs){

        cs = cs.toLowerCase();
        Boolean space_found = false;
        for(String c:cs.split("")){
            if(c.equals(" ")){
                space_found = true;
            }
        }

        String str_result = "";

        if(space_found==true){
            for (String sr : str_arry) {
                String sr2 = sr.toLowerCase();
                if(sr2.startsWith(cs)){
                    if (str_result.isEmpty()) {
                        str_result = sr;
                    } else {
                        str_result = str_result + "-" + sr;
                    }
                }
            }
        }else {

            for (String sr : str_arry) {
                String str_splited[] = sr.split(" ");
                for (String ss : str_splited) {
                    ss = ss.toLowerCase();
                    if (ss.startsWith(cs) || ss.equals(cs)) {
                        if (str_result.isEmpty()) {
                            str_result = sr;
                        } else {
                            str_result = str_result + "-" + sr;
                        }
                        break;
                    }
                }
            }

        }
        String arry_result[] = str_result.toLowerCase().split("-");
        Arrays.sort(arry_result);
        for (String ar : arry_result) {
            addCard(ar);
        }
        return "";
    }
    public void refreshView(){
        String sq_value = "";
        Cursor cursor = DB.view("id");
        if (cursor.getCount()!=0){
            while(cursor.moveToNext()){
                if (sq_value.isEmpty()) {
                    sq_value = cursor.getString(2).toString();
                } else {
                    sq_value = sq_value + "-" + cursor.getString(2).toString();
                }
            }
        }

        layout.removeAllViews();

        str_arry = sq_value.toLowerCase().split("-");
        Arrays.sort(str_arry);
        for (String sr : str_arry) {
            addCard(sr);
        }

    }
}