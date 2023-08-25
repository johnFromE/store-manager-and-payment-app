package com.example.qr;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class transAction {


    public String getTid(String user_phone){

            Date date = new Date();
            SimpleDateFormat f_date = new SimpleDateFormat("ddMMyyhhmmss");
            SimpleDateFormat f_date2 = new SimpleDateFormat("a");

            String current_time2="";

            if(f_date2.format(date).equals("pm")){
                current_time2 = "00";
            }else{
                current_time2 = "11";
            }

            String current_time = f_date.format(date) + current_time2 + user_phone;

            char[] ch_arr = current_time.toCharArray();
            String str_arr="";

            int i = 0 ;
            while(i < ch_arr.length){

                int two_digits = Integer.parseInt(ch_arr[i]+""+ch_arr[i+1])+65;

                if(two_digits<122) {
                    str_arr = str_arr + String.valueOf((char)two_digits);
                }else{
                    str_arr = str_arr + String.valueOf(two_digits);
                }

                i= i+2;
            }

            System.out.println(str_arr);
            return str_arr;
        }
}
