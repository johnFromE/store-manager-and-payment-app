package com.example.qr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.aviran.cookiebar2.CookieBar;

import java.io.IOException;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class scanner_layout extends AppCompatActivity {

    TextView item_name,  item_price,item,p_id,tax_Sec,total_value_Sec,item_count_tv;
    Boolean is_first = true;
    DBhelper DB;
    Dialog dialog;
    FirebaseDatabase db;
    ImageView qr_image;
    DatabaseReference reference;
    int count = 0,item_count=0;
    View scan_layout;
    LinearLayout layout,dialogLayout;
    BottomSheetDialog B_dialog;
    Button clear_items,cash_btn,mobile_pay,add_btn;
    String r_phone ="",hold_cashs="",hold_items="",last_amonut="",user_id="",Tid="",selected_item="",hold_price="",hold_amount="",hold_item="",hold_item_name="";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taste);
        //change colors
        toggleColorOfStatusBarIcons(this);


        B_dialog = new BottomSheetDialog(scanner_layout.this);
        item_name = findViewById(R.id.textContent);
        DB = new DBhelper(this);
        item_price = findViewById(R.id.textFormat);
        item =findViewById(R.id.productContent);
        p_id = findViewById(R.id.p_id);
        add_btn = findViewById(R.id.add_btn);
        item_count_tv = findViewById(R.id.item_count);
        scan_layout = findViewById(R.id.scan_layout);
        layout = findViewById(R.id.container);

        findViewById(R.id.sacnner_review_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDialog();
            }
        });

        // adding listener to the button
        scanCode();

        //dilog to view the qrcode for payment
        dialog = new Dialog(scanner_layout.this);
        dialog.setContentView(R.layout.view_qr_code);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_box));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.my_animation;


        qr_image = dialog.findViewById(R.id.qr_imgaeView);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();

            }
        });


        //end of main method ----------------------------
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
                Intent intent = new Intent(this,MainActivity .class);
                startActivity(intent);
                finish();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message

                //global_var = intentResult.getContents();
                p_id.setText(intentResult.getContents());
                item_price.setText("scanned");

                add_btn.setClickable(true);
                add_btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

                Cursor cursor = DB.select_item(intentResult.getContents());
                if (cursor.getCount()==0){
                    Toast.makeText(this,"No Data!",Toast.LENGTH_SHORT).show();
                }else{
                    while(cursor.moveToNext()){
                        if(intentResult.getContents().toString().equals(cursor.getString(0))) {
                            item.setText(cursor.getString(1));
                            item_name.setText(cursor.getString(2));
                            item_price.setText(cursor.getString(3));

                            if(selected_item.isEmpty()){
                                selected_item = cursor.getString(1) + ">>" +cursor.getString(3)+">>"+cursor.getString(2);
                            }else{
                                selected_item = selected_item + "--" + cursor.getString(1) + ">>" +cursor.getString(3)+">>"+cursor.getString(2);
                            }

                        }
                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                    Toast.makeText(scanner_layout.this, "no item selected", Toast.LENGTH_SHORT).show();
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

                TextView i_status = scanner_layout.this.findViewById(R.id.internet_status);
                if (total_value_Sec.getText().toString().equals("...")||total_value_Sec.getText().toString().isEmpty()) {
                    Toast.makeText(scanner_layout.this, "no item selected", Toast.LENGTH_SHORT).show();
                } else {
                    if(i_status.getText().toString().equals("No Internet connection")){
                        Toast.makeText(scanner_layout.this, i_status.getText().toString(), Toast.LENGTH_SHORT).show();
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
        dialog = new Dialog(scanner_layout.this);
        dialog.setContentView(R.layout.view_qr_code);
        dialog.getWindow().setBackgroundDrawable(scanner_layout.this.getDrawable(R.drawable.dialog_box));
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
        RelativeLayout relativeLayout = new RelativeLayout(scanner_layout.this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(5, 5, 5, 5);
        relativeLayout.setBackgroundColor(ContextCompat.getColor(scanner_layout.this, R.color.little_white));
        relativeLayout.setBackgroundColor(ContextCompat.getColor(scanner_layout.this, R.color.white));;
        relativeLayout.setLayoutParams(params);


        TextView nameView = new TextView(scanner_layout.this);
        nameView.setLayoutParams(new RelativeLayout.LayoutParams(250, RelativeLayout.LayoutParams.WRAP_CONTENT));
        nameView.setText(name);
        nameView.setTextColor(ContextCompat.getColor(scanner_layout.this, R.color.gary));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        nameView.setTypeface(nameView.getTypeface(), Typeface.BOLD);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setMaxLines(1);
        relativeLayout.addView(nameView);

        TextView priceView = new TextView(scanner_layout.this);
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
        LinearLayout linearLayout = new LinearLayout(scanner_layout.this);
        linearLayout.setGravity(Gravity.RIGHT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        linearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        linearLayout.setLayoutParams(linearLayoutParams);

        TextView textView = new TextView(scanner_layout.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText("1x");
        textView.setId(count);
        textView.setPadding(10,10,10,10);
        textView.setTextColor(scanner_layout.this.getResources().getColor(R.color.gary));
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_VERTICAL);

        count = count + 1;

        // Create the decrementButton ImageButton
        ImageButton decrementButton = new ImageButton(scanner_layout.this);
        decrementButton.setLayoutParams(new LinearLayout.LayoutParams(50, 70));
        decrementButton.setImageResource(R.drawable.left_vector);
        decrementButton.setBackground(ContextCompat.getDrawable(scanner_layout.this, R.drawable.arrow_btn));

        // Create the incrementButton ImageButton
        ImageButton incrementButton = new ImageButton(scanner_layout.this);
        incrementButton.setLayoutParams(new LinearLayout.LayoutParams(50, 70));
        incrementButton.setImageResource(R.drawable.right_vector);
        incrementButton.setBackground(ContextCompat.getDrawable(scanner_layout.this, R.drawable.arrow_btn));


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

        String hold_profit_this="";
        String hold_item_array[] = hold_item.split("-");
        Cursor cursor = DB.view("");
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String t_name = cursor.getString(1).replace(" ","_");
                String t_price = cursor.getString(3);
                String o_price = cursor.getString(4);
                for(String hi:hold_item_array){
                    if(t_name.toLowerCase().equals(hi.toLowerCase())){
                        if (hold_profit_this.isEmpty()){
                            hold_profit_this = (Float.parseFloat(t_price) - Float.parseFloat(o_price))+"";
                        }else{
                            hold_profit_this = hold_profit_this+"-"+(Float.parseFloat(t_price) - Float.parseFloat(o_price));
                        }

                    }
                }

            }
        }
        String hold_profit_array[] = hold_profit_this.split("-");

        String hold_amount_array[] = hold_amount.split("-");
        String hold_item_name_array[] = hold_item_name.split("-");

        for(int i=0 ; i<hold_profit_array.length ; i++ ){
            float first_num = Float.parseFloat(hold_profit_array[i]) ;
            float second_num = Float.parseFloat(hold_amount_array[i]) ;
            hold_profit_array[i] = (first_num * second_num) + "";
        }

        Date date = new Date();
        SimpleDateFormat f_date = new SimpleDateFormat("dd-MM-yyyy");
        String current_date = f_date.format(date);
        String date_arr[] = current_date.split("-");

        for(int i=0 ; i<hold_profit_array.length ; i++ ){
            String amount = DB.SelectFromStore(hold_item_array[i].toLowerCase(),hold_item_name_array[i].toLowerCase());
            int amount_num =Integer.parseInt(amount);
            int s_amount_num =Integer.parseInt(hold_amount_array[i]);

            if(amount_num<s_amount_num){
                Toast.makeText(scanner_layout.this, "insufficient amount", Toast.LENGTH_SHORT).show();
                hold_item = "";
                hold_profit_this = "";
            }else {
                int left_amount = amount_num - s_amount_num;
                DB.updateItemStore(hold_item_array[i], hold_item_name_array[i], "" + left_amount);

                //generating transaction id
                UUID uuid = UUID.randomUUID();
                String Tid_id = uuid.toString();

                DB.insert_sold_item(Tid_id, hold_item_array[i], hold_amount_array[i], hold_profit_array[i], date_arr[2], date_arr[1], date_arr[0]);
                hold_item = "";
                hold_profit_this = "";
                if(left_amount==0){
                    show_notification(hold_item_array[i],hold_item_name_array[i]);
                }
            }
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

            BitMatrix bitMatrix = multiFormatWriter.encode(r_phone+"><"+Tid+"><"+hold_item+"><"+hold_cashs+"><"+tax, BarcodeFormat.QR_CODE,1115,1127);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap firstImage = barcodeEncoder.createBitmap(bitMatrix);
            Bitmap secondImage = null;
            //qr_image.setImageBitmap(bitmap);

            try {
                InputStream bitmap1=scanner_layout.this.getAssets().open("qr_frame.png");
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
                    Toast.makeText(scanner_layout.this, "added to database", Toast.LENGTH_SHORT).show();
                    dialog.show();
                }
                else{
                    Toast.makeText(scanner_layout.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }
    public void show_notification(String item,String item_name){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CookieBar.build(scanner_layout.this)
                        .setTitle("Manage Sales")
                        .setMessage("The Item "+ item + "(" + item_name + ") " + "is sold out!")
                        .setBackgroundColor(Integer.parseInt("000000"))
                        .setCookiePosition(CookieBar.TOP)
                        .setDuration(5000)// Cookie will be displayed at the bottom
                        .show();

            }
        },500);
    }
}