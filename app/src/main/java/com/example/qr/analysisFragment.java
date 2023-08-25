package com.example.qr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.PieModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class analysisFragment extends Fragment {

    TextView top_saleing;
    PieChart pieChart;
    DBhelper DB;
    EditText view_history;
    LinearLayout top_saleing_layout ;
    TextView t_1,t_2,t_3,t_4,text_1,text_2,text_3,text_4;
    NumberPicker numberPicker;
    ValueLineChart line_chart;
    Handler handler;
    RelativeLayout month_holder_layout;
    Button month_btn;
    CardView cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);
        DB = new DBhelper(getActivity());

        line_chart = (ValueLineChart) root.findViewById(R.id.cubiclinechart);
        top_saleing_layout = root.findViewById(R.id.top_saleing_layout);
        top_saleing = root.findViewById(R.id.top_saleing_text);
        numberPicker = root.findViewById(R.id.number_picker);
        set_number_picker();
        Date date = new Date();
        SimpleDateFormat f_date = new SimpleDateFormat("MM");
        String c_month = f_date.format(date);
        numberPicker.setValue(Integer.parseInt(c_month));

        cardView = root.findViewById(R.id.month_btn);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // getRank(""+numberPicker.getValue());
            }
        });

        root.findViewById(R.id.more_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),table_activity.class);
                startActivity(intent);
            }
        });



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                if(cardView.getHeight()==80){
                    numberPicker.setVisibility(View.VISIBLE);
                    params.height = 300;
                }else{
                    set_number_picker();
                    params.height = 80;
                    numberPicker.setVisibility(View.VISIBLE);
                    line_chart.clearChart();
                    //Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
                    SecondThread thread_2 = new SecondThread();
                    thread_2.start();

                    read_in_main(numberPicker.getValue()+"");

                }
                cardView.setLayoutParams(params);
            }
        });

        view_history = root.findViewById(R.id.view_history);
        root.findViewById(R.id.clear_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB.Delete_History();
            }
        });

        handler = new Handler(Looper.getMainLooper());
        read_in_main(numberPicker.getValue()+"");
        SecondThread thread = new SecondThread();
        thread.start();

        return root;
    }
    public void read_in_main(String m){

        if(m.length()==1){
            m = 0+m;
        }

        String amount= "" ;
        String item= "" ;
        Cursor cursor = DB.viewHistory(m);
        if (cursor.getCount()!=0){
            while(cursor.moveToNext()){
                if(item.isEmpty()){
                    System.out.println(cursor.getString(6));
                    amount = cursor.getString(2);
                    item = cursor.getString(1).replace("_","");
                }else{
                    amount = amount+"-"+cursor.getString(2);
                    item = item +"-"+cursor.getString(1).replace("_","");
                }
            }

            String[][] top_saleing = combineDuplicateItems(item.split("-"),amount.split("-"));
            top_saleing_layout.removeAllViews();
            for(int i=0;i<top_saleing.length;i++){
                add_top_saleing(top_saleing[i][0],top_saleing[i][1]);
            }
        }
    }
    public static String[][] combineDuplicateItems(String[] items, String[] amounts) {
        HashMap<String, Integer> itemAmounts = new HashMap<String, Integer>();
        ArrayList<String> newItems = new ArrayList<String>();
        ArrayList<String> newAmounts = new ArrayList<String>();

        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            int amount = Integer.parseInt(amounts[i]);
            if (itemAmounts.containsKey(item)) {
                int existingAmount = itemAmounts.get(item);
                itemAmounts.put(item, existingAmount + amount);
            } else {
                itemAmounts.put(item, amount);
                newItems.add(item);
            }
        }

        for (String item : newItems) {
            newAmounts.add(String.valueOf(itemAmounts.get(item)));
        }

        String[][] result = new String[newItems.size()][2];
        for (int i = 0; i < newItems.size(); i++) {
            result[i][0] = newItems.get(i);
            result[i][1] = newAmounts.get(i);
        }

        return result;
    }
    public void add_top_saleing(String item_name,String item_amount) {
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        relativeLayout.setLayoutParams(layoutParams);

        TextView itemname_tv = new TextView(getContext());
        itemname_tv.setText(item_name);
        itemname_tv.setTextSize(20);
        itemname_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.gary));
        RelativeLayout.LayoutParams itemname_tvLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        itemname_tvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        itemname_tv.setLayoutParams(itemname_tvLayoutParams);

        TextView item_amount_tv = new TextView(getContext());
        item_amount_tv.setText(item_amount);
        item_amount_tv.setTextSize(20);
        item_amount_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.gary));
        RelativeLayout.LayoutParams item_amount_tvLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        item_amount_tvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        item_amount_tv.setLayoutParams(item_amount_tvLayoutParams);

        relativeLayout.addView(itemname_tv);
        relativeLayout.addView(item_amount_tv);
        top_saleing_layout.addView(relativeLayout);
    }
    public void set_number_picker(){
        String month="";
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

            numberPicker.setMinValue(Integer.parseInt(month_arr[0]));
            numberPicker.setMaxValue(Integer.parseInt(month_arr[month_arr.length-1]));

        }

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

    //thread-------------------------------------
    //-------------------------------------------
    public class SecondThread extends Thread {
        public void run() {


            read_history(numberPicker.getValue()+"");
        }
        public void read_history(String m){


            if(m.length()==1){
                m = 0+m;
            }
            String day = "";
            String amount = "";
            Cursor cursor = DB.viewHistory(m);
            if (cursor.getCount()!=0){
                while(cursor.moveToNext()){
                    if(day.isEmpty()){
                        System.out.println(cursor.getString(6));
                        day = cursor.getString(6);
                        amount = cursor.getString(2);
                    }else{
                        day = day +"-"+ cursor.getString(6);
                        amount = amount+"-"+cursor.getString(2);
                    }
                }

                String[][] data = getUniqueDaysAndAmounts(day.split("-"),amount.split("-"));
                add_label(data);
            }

        }


        public String[][] getUniqueDaysAndAmounts(String[] day, String[] amount) {
            Map<String, Integer> dayToAmountMap = new HashMap<>();

            // Iterate over the day and amount arrays
            for (int i = 0; i < day.length; i++) {
                // Check if the current day value is already in the map
                if (dayToAmountMap.containsKey(day[i])) {
                    // If so, add the current amount to the existing total for that day
                    dayToAmountMap.put(day[i], dayToAmountMap.get(day[i]) + Integer.parseInt(amount[i]));
                } else {
                    // If not, add the current day with its corresponding amount to the map
                    dayToAmountMap.put(day[i], Integer.parseInt(amount[i]));
                }
            }

            // Create a list of map entries and sort it based on the day values
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(dayToAmountMap.entrySet());
            Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                    System.out.println(e1.getKey());
                    return Integer.parseInt(e1.getKey()) - Integer.parseInt(e2.getKey());
                }
            });

            // Create new day and amount arrays with the sorted unique day values and their corresponding totals
            String[][] uniqueDaysAndAmounts = new String[sortedEntries.size()][2];
            int i = 0;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                uniqueDaysAndAmounts[i][0] = entry.getKey();
                uniqueDaysAndAmounts[i][1] = String.valueOf(entry.getValue());
                i++;
            }

            return uniqueDaysAndAmounts;
        }
        public void add_label(String[][] data){
            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFF56B7F1);
            String day="";
            float value=0;

            series.addPoint(new ValueLinePoint("first", 0));
            for(int i=0;i<data.length;i++) {
                day = data[i][0];
                value = Float.parseFloat(data[i][1]);
                series.addPoint(new ValueLinePoint(day, value));
            }
            series.addPoint(new ValueLinePoint(day, value));

            line_chart.addSeries(series);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    line_chart.startAnimation();
                }
            });
        }
    }

}
