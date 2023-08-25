package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class table_activity extends AppCompatActivity {

    DBhelper DB;
    NumberPicker numberPicker_year,numberPicker_month;
    LinearLayout tableLayout;
    String hold_amount="",hold_profit="",hold_date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        toggleColorOfStatusBarIcons(this);
        numberPicker_year = findViewById(R.id.number_picker_year);
        numberPicker_month = findViewById(R.id.number_picker_month);
        tableLayout = findViewById(R.id.table_layout);
        DB = new DBhelper(this);
        LinearLayout date_layout_2 = findViewById(R.id.date_layout_2);

        Button showDatePickerButton_2 = findViewById(R.id.showDatePickerButton_2);
        showDatePickerButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_layout_2.getVisibility() == View.VISIBLE) {
                    date_layout_2.setVisibility(View.GONE);
                } else {
                    date_layout_2.setVisibility(View.VISIBLE);
                }

                start_table("0"+numberPicker_month.getValue());


            }
        });

        String month[] = get_month();

        numberPicker_month.setMinValue(Integer.parseInt(month[0]));
        numberPicker_month.setMaxValue(5);
        numberPicker_year.setMinValue(2023);
        numberPicker_year.setMaxValue(2023);
        start_table(month[0]);
    }
    public void start_table(String m){

        String day[] = get_day(m);
        tableLayout.removeAllViews();
        System.out.println("-------------------------------");
        System.out.println(day.length);
        System.out.println(day[0]);


        if(!day[0].equals("0")) {
            for (String d : day) {
                Cursor cursor = DB.viewHistory_byDay(d, m);
                add_card(cursor);
            }
            table_activity.SecondThread thread = new table_activity.SecondThread();
            thread.start();
        }

    }
    public void add_card(Cursor cursor){
        // Get a reference to the TableLayout
        float num_amount=0,num_profit=0;

        CardView cardView = new CardView(this);
        CardView.LayoutParams card_params = new CardView.LayoutParams(680, CardView.LayoutParams.MATCH_PARENT);
        card_params.setMargins(20,20,20,20);
        cardView.setLayoutParams(card_params);
        cardView.setBackgroundColor(Color.parseColor("#FFBABABA"));

        ScrollView scrollView = new ScrollView(this);
        ScrollView.LayoutParams hView_params = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(hView_params);

        LinearLayout table = new LinearLayout(this);
        table.setOrientation(LinearLayout.VERTICAL);
        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout table_header = new LinearLayout(this);
        table_header.setOrientation(LinearLayout.HORIZONTAL);
        table_header.setGravity(Gravity.CENTER);
        table_header.setBackgroundColor(Color.parseColor("#404040"));
        table_header.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout table_body = new LinearLayout(this);
        table_body.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        table_body.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams header_params = new LinearLayout.LayoutParams(190, 50);
        header_params.setMargins(10,10,10,10);
        TextView tv_1 = new TextView(this);
        tv_1.setText("Item");
        tv_1.setTextSize(20);
        tv_1.setLayoutParams(header_params);
        table_header.addView(tv_1);
        TextView tv_2 = new TextView(this);
        tv_2.setText("Amount");
        tv_2.setTextSize(20);
        tv_2.setLayoutParams(header_params);
        table_header.addView(tv_2);
        TextView tv_3 = new TextView(this);
        tv_3.setText("Profit");
        tv_3.setTextSize(20);
        tv_3.setLayoutParams(header_params);
        table_header.addView(tv_3);
        table.addView(table_header);

        //String arr_four[][] = {{"laptop","6","70","200"}};
        while(cursor.moveToNext()){
            LinearLayout table_raw = new LinearLayout(this);
            table_raw.setOrientation(LinearLayout.HORIZONTAL);
            table_raw.setGravity(Gravity.CENTER);
            table_raw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

            for(int j=1;j<4;j++){
                TextView itemTextView = new TextView(this);
                itemTextView.setText(cursor.getString(j));
                itemTextView.setTextSize(20);
                itemTextView.setLayoutParams(header_params);
                table_raw.addView(itemTextView);
            }
            num_amount = num_amount + Float.parseFloat(cursor.getString(2));
            num_profit = num_profit + Float.parseFloat(cursor.getString(3));
            table_body.addView(table_raw);
        }

        if(hold_amount.isEmpty()){
            hold_amount = "" + num_amount;
            hold_profit = "" + num_profit;
            //hold_date = cursor.getString(4) + "/" + cursor.getString(5) + "/" + cursor.getString(6);
        }else{
            hold_amount = hold_amount + "-" + num_amount;
            hold_profit = hold_profit + "-" + num_profit;
            //hold_date = hold_date + "-" + cursor.getString(4) + "/" + cursor.getString(5) + "/" + cursor.getString(6);
        }

        scrollView.addView(table_body);
        table.addView(scrollView);
        cardView.addView(table);
        tableLayout.addView(cardView);
    }
    public String[] get_month(){
        String month="";
        String month_t[]={"0"};
        Cursor cursor = DB.viewMonth();
        if (cursor.getCount()!=0) {
            while (cursor.moveToNext()){
                if(month.isEmpty()){
                    month = cursor.getString(5);
                }else{
                    month = month+"-"+cursor.getString(5);
                }
            }
            String month_arr[] =  getDistinctElements(month.split("-"));
            return  month_arr;
        }
        return month_t;
    }
    public String[] get_day(String m){
            String day="";
            String day_t[]={"0"};
            Cursor cursor = DB.viewHistory(m);
            if (cursor.getCount()!=0) {
                while (cursor.moveToNext()){
                    if(day.isEmpty()){
                        day = cursor.getString(6);
                    }else{
                        day = day+"-"+cursor.getString(6);
                    }
                }
                String day_arr[] =  getDistinctElements(day.split("-"));
                return  day_arr;
            }
            return day_t;
    }
    public String[] getDistinctElements(String[] arry) {

        // Create a set to store distinct elements
        Set<String> set = new HashSet<String>();

        // Add all elements from array to set
        for(int i = 0; i < arry.length; i++) {
            set.add(arry[i]);
        }

        // Create a new array with distinct elements
        String[] distinctArry = new String[set.size()];
        int i = 0;
        for(String element : set) {
            distinctArry[i++] = element;
        }

        // Return the distinct elements array
        return distinctArry;
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
    //thread
    public class SecondThread extends Thread {
        public void run() {
            refresh();
        }
        public void refresh(){
            String month[] = get_month();
            String day[] = get_day(month[0]);
            HorizontalScrollView hsv = findViewById(R.id.horizontal_sv);
            TextView date = findViewById(R.id.text_temp);
            TextView amount = findViewById(R.id.Total_amount);
            TextView profit = findViewById(R.id.Total_profit);

            int x_axis = hsv.getScrollX();
            if(x_axis==0 || x_axis<500){
                date.setText("Date: 2023/"+month[0]+"/"+day[0]);
                amount.setText("Total Amount: "+hold_amount.split("-")[0]);
                profit.setText("Total Amount: "+hold_profit.split("-")[0]);
            }
            hsv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {

                    int x_axis = hsv.getScrollX();
                    if(x_axis==0 || x_axis<500){
                        date.setText("Date: 2023/"+month[0]+"/"+day[0]);
                        amount.setText("Total Amount: "+hold_amount.split("-")[0]);
                        profit.setText("Total Amount: "+hold_profit.split("-")[0]);
                    }else{
                        float x_axis_2 = x_axis /700;
                        if(x_axis_2 % 1 == 0 && x_axis_2 != 0){
                            int day_n = Math.round(x_axis_2);
                            date.setText("Date: 2023/"+month[0]+"/"+day[day_n]);
                            amount.setText("Total Amount: "+hold_amount.split("-")[day_n]);
                            profit.setText("Total Amount: "+hold_profit.split("-")[day_n]);
                        }
                    }

                    // Toast.makeText(table_activity.this, , Toast.LENGTH_SHORT).show();
                }
            });
        }
        public float get_db(String m,String d,int num){

            float sum = 0 ;
            Cursor cursor = DB.viewHistory_byDay(d,m);
            while (cursor.moveToNext()){
                sum = sum + Float.parseFloat(cursor.getString(num));
            }
            return sum;
        }
    }
}