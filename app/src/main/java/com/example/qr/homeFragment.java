package com.example.qr;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.aviran.cookiebar2.CookieBar;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class homeFragment extends Fragment {

    DBhelper DB;
    View scan_layout;
    EditText inputSearch;
    LinearLayout layout;
    String str_arry[],selected_item="";
    TextView item_count_tv;
    int item_count=0;
    BottomSheetDialog B_dialog;
    private LinearLayout dialogLayout;
    int count=0;
    String hold_price="",hold_amount="",hold_item="",hold_item_name="",hold_profit="";
    TextView total_value_Sec;
    TextView tax_Sec;
    Button clear_items,cash_btn,mobile_pay;
    String r_phone ="",last_tag="",phoneNumber="",message="",hold_items="",last_amonut="",user_id="",Tid="";
    FirebaseDatabase db;
    ImageView qr_image;
    DatabaseReference reference;
    Boolean is_first = true;
    transAction TA;
    Button add_btn;
    Dialog dialog;
    Context mContext;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    SwipeRefreshLayout swipe_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        DB = new DBhelper(getActivity());
        //temp();

        //dilog to view the qrcode for payment
        B_dialog = new BottomSheetDialog(getActivity());

        swipe_layout = root.findViewById(R.id.swipe_layout);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refearsh_view();
                swipe_layout.setRefreshing(false);
            }
        });

        DB = new DBhelper(getActivity());
        item_count_tv = root.findViewById(R.id.item_count);
        scan_layout = (View)root.findViewById(R.id.scan_layout);
        layout = root.findViewById(R.id.container);
        scan_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), scanner_layout.class);
                startActivity(intent);
            }
        });


        root.findViewById(R.id.settingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Setting.class);
                startActivity(intent);
            }
        });


        // view item list---------------------------
        refearsh_view();


        inputSearch = (EditText) root.findViewById(R.id.itemSearch);

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


        root.findViewById(R.id.review_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    createDialog();
            }
        });


        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);
                int screenHeight = root.getHeight();
                if (950 > screenHeight) {
                    scan_layout.setVisibility(View.GONE);
                } else {
                    scan_layout.setVisibility(View.VISIBLE);
                }
            }
        });






        return root;
        //end of main activity--------------------------------
        //----------------------------------------------------
    }

    private void refearsh_view() {

        layout.removeAllViews();
        String sq_value = "";

        Cursor cursor = DB.view("id");
        if (cursor.getCount()!=0){
            while(cursor.moveToNext()){
                if (sq_value.isEmpty()) {
                    sq_value = cursor.getString(1).toString()+">>"+cursor.getString(3).toString()+">>"+cursor.getString(2).toString();
                } else {
                    sq_value = sq_value + "-" + cursor.getString(1).toString()+">>"+cursor.getString(3).toString()+">>"+cursor.getString(2).toString();
                }
            }
            str_arry = sq_value.toLowerCase().split("-");
            Arrays.sort(str_arry);

            for (String sr : str_arry) {
                String sr_arr[] = sr.split(">>");
                addCard(sr_arr[0],sr_arr[1],sr_arr[2]);
            }
        }
    }

    private void addCard(String name,String price,String item_name) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(20,0,0,0);

        RelativeLayout.LayoutParams params_2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_2.setMargins(10,10,10,0);
        RelativeLayout itemview = new RelativeLayout(getActivity());
        itemview.setPaddingRelative(10,10,10,10);
        itemview.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        itemview.setLayoutParams(params_2);

        RelativeLayout.LayoutParams params_3 = new RelativeLayout.LayoutParams(300, 50);
        params_3.setMargins(110,10,0,0);
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(params_3);
        textView.setText(name.toUpperCase());
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#2B2B2B"));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxLines(1);
        itemview.addView(textView);

        String name_arr[] =name.split("");
        String image_latter = name_arr[0].toUpperCase() + name_arr[1];

        TextView textView1 = new TextView(getActivity());
        textView1.setLayoutParams(new RelativeLayout.LayoutParams(80, 80));
        textView1.setText(image_latter);
        textView1.setTextSize(25);
        textView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView1.setPadding(5,5,5,5);
        textView1.setBackgroundColor(Color.parseColor(getColor(image_latter)));
        textView1.setTextColor(Color.parseColor("#FFFFFFFF"));
        itemview.addView(textView1);

        RelativeLayout.LayoutParams amount_params = new RelativeLayout.LayoutParams(200,RelativeLayout.LayoutParams.MATCH_PARENT);
        amount_params.setMargins(110,55,0,0);
        TextView item_name_tv = new TextView(getActivity());
        item_name_tv.setLayoutParams(amount_params);
        item_name_tv.setText(item_name.toUpperCase());
        item_name_tv.setTextSize(10);
        item_name_tv.setTextColor(Color.parseColor("#2B2B2B"));
        itemview.addView(item_name_tv);

        TextView text_price = new TextView(getActivity());
        text_price.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        text_price.setTextSize(20);
        text_price.setText(price+ " Birr");
        text_price.setTextColor(Color.parseColor("#404040"));
        text_price.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        text_price.setGravity(Gravity.RIGHT);
        text_price.setPadding(0,15,5,0);
        itemview.addView(text_price);


        //vibrate on click
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        itemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final VibrationEffect vibrationEffect2;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    // create vibrator effect with the constant EFFECT_CLICK
                    vibrationEffect2 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);

                    // it is safe to cancel other vibrations currently taking place
                    vibrator.cancel();

                    vibrator.vibrate(vibrationEffect2);
                }
                item_count++;
                item_count_tv.setText("items: " + item_count);

                if(selected_item.isEmpty()){
                    selected_item = textView.getText().toString() + ">>" +text_price.getText().toString()+">>"+item_name_tv.getText().toString();
                }else{
                    selected_item = selected_item + "--" + textView.getText().toString() + ">>" +text_price.getText().toString() + ">>"+item_name_tv.getText().toString();
                }
            }
        });

        layout.addView(itemview);
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
            if(ar.length()==0){

            }else {
                String ar_arr[] = ar.split(">>");
                addCard(ar_arr[0], ar_arr[1],ar_arr[2]);
            }
        }
        return "";
    }
    private static String getColor(String str) {

        char latter = str.charAt(0);

        int[] arr1 = {65,70,75,80,85,90};
        int[] arr2 = {66,71,76,81,86};
        int[] arr3 = {67,72,77,82,87};
        int[] arr4 = {68,73,78,83,88};
        int[] arr5 = {69,74,79,84,89};

        int num=0;
        for(int a : arr1){if(a==(int)latter){num = 1;break; }}
        for(int a : arr2){if(a==(int)latter){num = 2;break; }}
        for(int a : arr3){if(a==(int)latter){num = 3;break; }}
        for(int a : arr4){if(a==(int)latter){num = 4;break; }}
        for(int a : arr5){if(a==(int)latter){num = 5;break; }}

        if(num==1){
            return "#ffe08a";
        } else if (num==2) {
            return "#ff8a8a";
        } else if (num==3) {
            return "#8aff99";
        } else if (num==4) {
            return "#8ae9ff";
        }else {
            return "#ad8aff";
        }
    }

    public void recite(){
        //-----------------------------------------
        //dialog box ------------------------------
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.recite_layout);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;

        EditText r_Phone = dialog.findViewById(R.id.r_phone);
        Button send = dialog.findViewById(R.id.recite_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the SMS permission is already granted
                phoneNumber =  r_Phone.getText().toString();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS)==0) {
                    // Permission is already granted, proceed with your app logic
                    sendSMS();
                } else {
                    // Permission is not granted, request it
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
                }
            }
        });
        Button cancel = dialog.findViewById(R.id.recite_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults[1] == 0) {
                sendSMS();
            } else {
                Toast.makeText(getActivity(), "Phone CALL permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSMS() {

        Date date = new Date();
        SimpleDateFormat f_date = new SimpleDateFormat("dd/MM/yyyy");
        String current_date = f_date.format(date);

        if(isValidPhoneNumber((phoneNumber))){
            try {
                message = message +"\n\ndate: "+  current_date;

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(getActivity().getApplicationContext(), "recite sent.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "SMS sending failed.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(mContext, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression pattern for a phone number
        String pattern = "^09\\d{8}$";

        // Check if the input matches the pattern
        return phoneNumber.matches(pattern);
    }

















    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    //second activity_______________________________________________________
    private void createDialog() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout,null,false);
        B_dialog.setContentView(view);
        B_dialog.show();

        dialogLayout = view.findViewById(R.id.dialog_layout);
        total_value_Sec = view.findViewById(R.id.total_value_sec);
        tax_Sec = view.findViewById(R.id.tax_tv_sec);
        clear_items = view.findViewById(R.id.clear_items_btn);
        cash_btn = view.findViewById(R.id.cash_btn);

        if(!selected_item.isEmpty()) {
            setSItem(selected_item);
        }
        clear_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogLayout.removeAllViews();
                hold_amount="";
                hold_price="";
                selected_item="";
                total_value_Sec.setText("...");
                tax_Sec.setText("...         ...");
                count = 0;
                item_count_tv.setText("");
                item_count = 0;

            }
        });


        cash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(total_value_Sec.getText().toString().equals("...")||total_value_Sec.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "no item selected", Toast.LENGTH_SHORT).show();
                }else {
                    store_hitory();
                    cash_btn.setTextColor(Color.parseColor("#10FF05"));
                    cash_btn.setText("saved");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cash_btn.setTextColor(Color.parseColor("#FFFFFF"));
                            cash_btn.setText("cash");
                        }
                    }, 1000);

                    dialogLayout.removeAllViews();
                    hold_amount="";
                    hold_price="";
                    selected_item="";
                    total_value_Sec.setText("...");
                    tax_Sec.setText("...         ...");
                    count = 0;
                    item_count_tv.setText("");
                    item_count = 0;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        B_dialog.dismiss();
                    }
                },100);
            }
        });


        gn_qr();
        mobile_pay = view.findViewById(R.id.mobile_pay_btn);
        mobile_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView i_status = getActivity().findViewById(R.id.internet_status);
                    if (total_value_Sec.getText().toString().equals("...")||total_value_Sec.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "no item selected", Toast.LENGTH_SHORT).show();
                    } else {
                        if(i_status.getText().toString().equals("No Internet connection")){
                            Toast.makeText(getActivity(), i_status.getText().toString(), Toast.LENGTH_SHORT).show();
                        }else {
                            mobile_pay.setTextColor(Color.parseColor("#10FF05"));
                            mobile_pay.setText("wait...");

                            //generating universal ID
                            UUID uuid = UUID.randomUUID();
                            Tid = uuid.toString();

                            transAction(Tid);
                            dialogLayout.removeAllViews();
                            hold_amount = "";
                            hold_price = "";
                            selected_item = "";
                            total_value_Sec.setText("...");
                            tax_Sec.setText("...         ...");
                            count = 0;
                            item_count_tv.setText("");
                            item_count = 0;
                        }
                    }
            }
        });

    }
    public void gn_qr() {
        //dilog to view the qrcode for payment
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.view_qr_code);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;


        qr_image = dialog.findViewById(R.id.qr_imgaeView);


        dialog.findViewById(R.id.close_d_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.findViewById(R.id.done_btn_o).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(Tid).child("done").setValue("YES").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Not Saved", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        if(is_first) {
                            hold_amount = last_amonut;
                            is_first =false;
                        }else{
                            hold_amount = hold_amount + "--" + last_amonut;
                        }

                        //----------------------
                        //register History------
                        store_hitory();
                        hold_amount = "";

                    }
                });
            }
        });

    }
    public void setSItem(String selectedItem) {

        Boolean pass = false;
        for(String si:selectedItem.split("")){
            if(si.equals("-")){
                pass = true;
            }
        }
        if(pass) {
            String str_arr[] = selectedItem.split("--");
            for (String sa : str_arr) {
                String sa_arr[] = sa.split(">>");
                addItem(sa_arr[0], sa_arr[1],sa_arr[2]);
            }
        }else{
            String sa_arr[] = selectedItem.split(">>");
            addItem(sa_arr[0], sa_arr[1],sa_arr[2]);
        }
    }
    public void addItem(String name, String price,String item_name) {

        if(hold_price.isEmpty()){
            hold_price = price.replace(" Birr","");
        }else{
            hold_price = hold_price + "--" +price.replace(" Birr","");
        }

        if(hold_amount.isEmpty()){
            hold_amount = "1";
        }else{
            hold_amount = hold_amount + "-1";
        }

        if(hold_item.isEmpty()){
            hold_item = name.replace(" ","_");
        }else{
            hold_item = hold_item + "-" + name.replace(" ","_");
        }

        if(hold_item_name.isEmpty()){
            hold_item_name = item_name;
        }else{
            hold_item_name = hold_item_name + "-" + item_name;
        }



        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(5, 5, 5, 5);
        relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.little_white));
        relativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));;
        relativeLayout.setLayoutParams(params);


        TextView nameView = new TextView(getActivity());
        nameView.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
        nameView.setText(name);
        nameView.setTextColor(ContextCompat.getColor(getActivity(), R.color.gary));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        nameView.setTypeface(nameView.getTypeface(), Typeface.BOLD);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setMaxLines(1);
        relativeLayout.addView(nameView);

        TextView priceView = new TextView(getActivity());
        RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        priceParams.setMargins(0,0,200,0);
        priceView.setLayoutParams(priceParams);
        priceView.setTextSize(20);
        priceView.setText(price);
        priceView.setTextColor(Color.parseColor("#404040"));
        priceView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        priceView.setGravity(Gravity.RIGHT);
        priceView.setPadding(0, 10, 5, 0);
        relativeLayout.addView(priceView);


        // Create a LinearLayout and set its orientation to horizontal
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setGravity(Gravity.RIGHT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        linearLayout.setLayoutParams(linearLayoutParams);

        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText("1x");
        textView.setId(count);
        textView.setPadding(10,10,10,10);
        textView.setTextColor(getActivity().getResources().getColor(R.color.gary));
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        count = count + 1;

        // Create the decrementButton ImageButton
        ImageButton decrementButton = new ImageButton(getActivity());
        decrementButton.setLayoutParams(new LinearLayout.LayoutParams(50, 70));
        decrementButton.setImageResource(R.drawable.left_vector);
        decrementButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arrow_btn));

        // Create the incrementButton ImageButton
        ImageButton incrementButton = new ImageButton(getActivity());
        incrementButton.setLayoutParams(new LinearLayout.LayoutParams(50, 70));
        incrementButton.setImageResource(R.drawable.right_vector);
        incrementButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.arrow_btn));


        linearLayout.addView(decrementButton);
        linearLayout.addView(textView);
        linearLayout.addView(incrementButton);


        add_price();
// Set onClickListener for the increment button
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentValue = textView.getText().toString();
                int currentValueInt = Integer.parseInt(currentValue.substring(0, currentValue.length() - 1));
                currentValueInt++;
                textView.setText(currentValueInt + "x");
                String arr_temp[] = hold_amount.split("-");
                int num = Integer.parseInt(arr_temp[textView.getId()]);
                num++;
                arr_temp[textView.getId()] =  num +"";

                hold_amount = Arrays.toString(arr_temp);
                hold_amount = hold_amount.replace("[","");
                hold_amount = hold_amount.replace("]","");
                hold_amount = hold_amount.replace(", ","-");

                add_price();

            }
        });

// Set onClickListener for the decrement button
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentValue = textView.getText().toString();
                int currentValueInt = Integer.parseInt(currentValue.substring(0, currentValue.length() - 1));
                currentValueInt--;

                String arr_temp[] = hold_amount.split("-");
                int num = Integer.parseInt(arr_temp[textView.getId()]);
                num--;

                if (currentValueInt < 1) {
                    currentValueInt = 1;
                    num=1;
                }
                textView.setText(currentValueInt + "x");
                arr_temp[textView.getId()] =  num +"";

                hold_amount = Arrays.toString(arr_temp);
                hold_amount = hold_amount.replace("[","");
                hold_amount = hold_amount.replace("]","");
                hold_amount = hold_amount.replace(", ","-");

                add_price();
            }
        });

        relativeLayout.addView(linearLayout);



        if (dialogLayout != null) {
            dialogLayout.addView(relativeLayout);
        } else {
            // Handle null dialogLayout object
        }
    }
    public void add_price(){

        String price_arry[] = hold_price.split("--");
        String amount_arry[] = hold_amount.split("-");
        float total_price = 0;

        for(int i=0;i<price_arry.length;i++){
            if(total_price == 0){
                total_price = Float.parseFloat(price_arry[i]) * Float.parseFloat(amount_arry[i]);
            }else {
                float num2 = Float.parseFloat(price_arry[i]) * Float.parseFloat(amount_arry[i]);
                total_price = total_price + num2 ;
            }

        }

        int tax_p=0;
        Cursor cursor = DB.view_tax("");
        if(cursor.getCount()>0) {
            while(cursor.moveToNext()) {
                tax_p = Integer.parseInt(cursor.getString(0));
            }
            float tax = (total_price * 5) / 100;
            total_value_Sec.setText((total_price + tax)+" Birr");
            tax_Sec.setText("Price: "+total_price+"        "+tax_p+"%Tax: "+tax);
        }else{
            total_value_Sec.setText(total_price+" Birr");
            tax_Sec.setText("Price: "+total_price+"          No Tax");
        }




    }
    public Boolean store_hitory(){

        try {
            String hold_profit_this = "";
            String hold_item_array[] = hold_item.split("-");
            Cursor cursor = DB.view("");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String t_name = cursor.getString(1).replace(" ", "_");
                    String t_price = cursor.getString(3);
                    String o_price = cursor.getString(4);
                    for (String hi : hold_item_array) {
                        if (t_name.toLowerCase().equals(hi.toLowerCase())) {
                            if (hold_profit_this.isEmpty()) {
                                hold_profit_this = (Float.parseFloat(t_price) - Float.parseFloat(o_price)) + "";
                            } else {
                                hold_profit_this = hold_profit_this + "-" + (Float.parseFloat(t_price) - Float.parseFloat(o_price));
                            }

                        }
                    }

                }
            }
            String hold_profit_array[] = hold_profit_this.split("-");

            String hold_amount_array[] = hold_amount.split("-");
            String hold_item_name_array[] = hold_item_name.split("-");

            message = "manage sales recite\n -------------------- \n"+ hold_item + "\n" + hold_item_name + "\n" + hold_price;

            System.out.println("------------------>> length: " + hold_profit_array.length);
            for (int i = 0; i < hold_profit_array.length; i++) {
                float first_num = Float.parseFloat(hold_profit_array[i]);
                float second_num = Float.parseFloat(hold_amount_array[i]);
                hold_profit_array[i] = (first_num * second_num) + "";
            }

            Date date = new Date();
            SimpleDateFormat f_date = new SimpleDateFormat("dd-MM-yyyy");
            String current_date = f_date.format(date);
            String date_arr[] = current_date.split("-");

            for (int i = 0; i < hold_profit_array.length; i++) {
                String amount = DB.SelectFromStore(hold_item_array[i].toLowerCase(), hold_item_name_array[i].toLowerCase());
                int amount_num = Integer.parseInt(amount);
                int s_amount_num = Integer.parseInt(hold_amount_array[i]);

                if (amount_num < s_amount_num) {
                    Toast.makeText(getActivity(), "insufficient amount", Toast.LENGTH_SHORT).show();
                    hold_item = "";
                    hold_profit_this = "";
                } else {
                    int left_amount = amount_num - s_amount_num;
                    DB.updateItemStore(hold_item_array[i], hold_item_name_array[i], "" + left_amount);

                    //generating transaction id
                    UUID uuid = UUID.randomUUID();
                    String Tid_id = uuid.toString();

                    DB.insert_sold_item(Tid_id, hold_item_array[i], hold_amount_array[i], hold_profit_array[i], date_arr[2], date_arr[1], date_arr[0]);
                    hold_item = "";
                    hold_profit_this = "";
                    if (left_amount == 0) {
                        show_notification(hold_item_array[i], hold_item_name_array[i]);
                    }
                }
            }

            recite();
        }catch (Exception e){
            System.out.println(e);
        }
        return true;
    }
    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 10, 10, null);
        return result;
    }
    public Boolean transAction(String Tid){

        Cursor cursor = DB.viewUser();
        if(cursor.getCount()!=0) {
            while (cursor.moveToNext()) {
                r_phone = cursor.getString(3);
                user_id = cursor.getString(5);
            }
        }

        //qr_image ganrating
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            float tax = 0;

            BitMatrix bitMatrix = multiFormatWriter.encode(r_phone+"><"+Tid+"><"+hold_item+"><"+hold_price+"><"+tax, BarcodeFormat.QR_CODE,1115,1127);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap firstImage = barcodeEncoder.createBitmap(bitMatrix);
            Bitmap secondImage = null;
            //qr_image.setImageBitmap(bitmap);

            try {
                InputStream bitmap1=getActivity().getAssets().open("qr_frame.png");
                secondImage = BitmapFactory.decodeStream(bitmap1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Bitmap mergedImages = createSingleImageFromMultipleImages(firstImage, secondImage);

            qr_image.setImageBitmap(mergedImages);


        } catch (WriterException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        SimpleDateFormat f_date = new SimpleDateFormat("YYYY-MM-dd");
        String c_month[] = f_date.format(date).split("-");

        HelperCalss2 tranAction = new HelperCalss2(Tid,"null",r_phone,hold_items,count+"","total_value",0+"","NO");
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("tax_info/"+user_id+"/"+c_month[0]+"/"+c_month[1]+"/"+c_month[2]+"/");

        //passing to firbase
        reference.child(Tid).setValue(tranAction).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    mobile_pay.setText("via mobile");
                    mobile_pay.setTextColor(Color.parseColor("#FFFFFF"));
                    Toast.makeText(getActivity(), "added to database", Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
                else{
                    Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return true;
    }
    public void temp(){

        String[][] electronicsArray = {
                {"Smartphone", "4", "79.99"},
                {"Laptop", "3", "89.99"},
                {"Tablet", "1", "59.99"},
                {"Smartwatch", "5", "99.99"},
                {"Television", "2", "69.99"},
                {"Wireless Headphones", "1", "59.99"},
                {"Bluetooth Speaker", "5", "89.99"},
                {"Gaming Console", "2", "99.99"},
                {"Smart Home Hub", "4", "79.99"},
                {"Fitness Tracker", "3", "69.99"}
        };
        String[] year = {"2023"};
        String[] month = {"01","02","03"};
        for (String y : year){
            for(String m: month){
                for (int d = 1; d < 30; d++){
                    for (int i = 0; i < electronicsArray.length; i++){
                        Random rand = new Random();
                        DB.insert_sold_item("000",electronicsArray[i][0],""+rand.nextInt(10)+1,""+rand.nextInt(1000)+100,y,m,d+"");
                    }
                }
            }
        }

    }

    public void show_notification(String item,String item_name){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CookieBar.build(getActivity())
                        .setTitle("Manage Sales")
                        .setMessage("The Item "+ item + "(" + item_name + ") " + "is sold out!")
                        .setBackgroundColor(Integer.parseInt("000000"))
                        .setCookiePosition(CookieBar.TOP)
                        .setDuration(5000)
                        .show();

            }
        },5000);
    }


}
