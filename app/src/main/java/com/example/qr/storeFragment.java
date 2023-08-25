package com.example.qr;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

public class storeFragment extends Fragment {

    ImageButton add_item_btn;
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
    SwipeRefreshLayout swipe_layout_2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store, container, false);

        swipe_layout_2 = root.findViewById(R.id.swipe_layout_2);
        swipe_layout_2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshView();
                swipe_layout_2.setRefreshing(false);
            }
        });

        add_item_btn = root.findViewById(R.id.add_item_btn);
        add_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),store.class);
                startActivity(intent);
            }
        });

        DB = new DBhelper(getActivity());
        String sq_value = "";

        Cursor cursor = DB.view("id");
        if (cursor.getCount()!=0){
            while(cursor.moveToNext()){
                if (sq_value.isEmpty()) {
                    sq_value = cursor.getString(1).toString() + "=" + cursor.getString(5) +"    Brand: "+ cursor.getString(2);
                } else {
                    sq_value = sq_value + "-" + cursor.getString(1).toString() + "=" + cursor.getString(5) + "    Brand: "+ cursor.getString(2);
                }
            }
        }

        str_arry = sq_value.toLowerCase().split("-");
        Arrays.sort(str_arry);

        layout = root.findViewById(R.id.container);

        inputSearch = (EditText) root.findViewById(R.id.itemSearch);

        for (String sr : str_arry) {
            if(sr.isEmpty()) {

            }else{
                addCard(sr);
            }
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

        return root;
    }

    private void addCard(String name) {

        String arr_str[] = name.split("=");
        name = arr_str[0];
        String amount = arr_str[1];

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 80);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(20,0,0,0);

        RelativeLayout.LayoutParams params_2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_2.setMargins(0,0,0,10);
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setPaddingRelative(10,10,5,10);
        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        relativeLayout.setLayoutParams(params_2);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new RelativeLayout.LayoutParams(200, RelativeLayout.LayoutParams.MATCH_PARENT));
        textView.setText(name.toUpperCase());
        textView.setTextSize(25);
        textView.setTextColor(Color.parseColor("#2B2B2B"));
        relativeLayout.addView(textView);

        RelativeLayout.LayoutParams amount_params = new RelativeLayout.LayoutParams(400,RelativeLayout.LayoutParams.MATCH_PARENT);
        amount_params.setMargins(0,60,0,0);
        TextView amount_tv = new TextView(getActivity());
        amount_tv.setLayoutParams(amount_params);
        amount_tv.setText("amount: "+ amount);
        amount_tv.setTextSize(10);
        amount_tv.setTextColor(Color.parseColor("#2B2B2B"));
        relativeLayout.addView(amount_tv);

        delete_btn = new Button(getActivity());
        delete_btn.setLayoutParams(params);
        delete_btn.setText("delete");
        delete_btn.setBackground(getActivity().getDrawable(R.drawable.gary_box));
        relativeLayout.addView(delete_btn);


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                DB.deleteItem(item);
                refreshView();
                Toast.makeText(getActivity(), item+ " :( deleted", Toast.LENGTH_SHORT).show();
            }
        });

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.view_item);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("here--------------------------------");
                System.out.println(amount_tv.getText().toString());
                String item = textView.getText().toString();
                String item_name = amount_tv.getText().toString().split("    brand: ")[1];


                EditText d_item_id = dialog.findViewById(R.id.diloge_item_id);
                EditText d_item = dialog.findViewById(R.id.dilog_item);
                EditText d_name = dialog.findViewById(R.id.dilog_item_name);
                EditText d_price = dialog.findViewById(R.id.dilog_item_price);
                EditText d_saleing_price = dialog.findViewById(R.id.dilog_Saleing_price);
                EditText d_amount = dialog.findViewById(R.id.dilog_amount);
                Button update_btn = dialog.findViewById(R.id.update_dilog_button);

                Cursor cursor = DB.SelectFromStore_two(item,item_name);
                while(cursor.moveToNext()){
                    d_item_id.setText(cursor.getString(0).toString());
                    d_item.setText(cursor.getString(1).toString());
                    d_name.setText(cursor.getString(2).toString());
                    d_saleing_price.setText(cursor.getString(3).toString());
                    d_price.setText(cursor.getString(4).toString());
                    d_amount.setText(cursor.getString(5).toString());
                }
                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(d_item_id.getText().toString().isEmpty()||d_item.getText().toString().isEmpty()||d_name.getText().toString().isEmpty()||d_price.getText().toString().isEmpty()||d_saleing_price.getText().toString().isEmpty()||d_amount.getText().toString().isEmpty()){
                            Toast.makeText(getActivity(), "Empty field", Toast.LENGTH_SHORT).show();
                        }else {
                            DB.updateItemStore_two(d_item_id.getText().toString(),
                                    d_item.getText().toString(),
                                    d_name.getText().toString(),
                                    d_price.getText().toString(),
                                    d_saleing_price.getText().toString(),
                                    d_amount.getText().toString());
                            update_btn.setText("Updated");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   update_btn.setText("update");
                                }
                            },1500);
                        }
                    }
                });

                dialog.show();
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
                    sq_value = cursor.getString(1).toString() + "=" + cursor.getString(5) +"    Brand: "+ cursor.getString(2);
                } else {
                    sq_value = sq_value + "-" + cursor.getString(1).toString() + "=" + cursor.getString(5) + "    Brand: "+ cursor.getString(2);
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