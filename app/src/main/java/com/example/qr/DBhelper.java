package com.example.qr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Locale;


public class DBhelper extends SQLiteOpenHelper {

    public DBhelper(Context context) {
        super(context, "DB_store.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create table item_store_two (id TEXT,item TEXT,name TEXT,price TEXT,orignal_price TEXT,amount TEXT)");
        DB.execSQL("create table sold_item (item_id TEXT,item TEXT,amount TEXT,profit TEXT,year TEXT,month TEXT,day TEXT)");
        DB.execSQL("create table user_info (username TEXT,password TEXT,user_type TEXT,phone_number TEXT,cbe_birr_pin TEXT,user_id TEXT)");
        DB.execSQL("create table pin_store (pin TEXT)");
        DB.execSQL("create table Tid_store (Tid TEXT)");
        DB.execSQL("create table tax_value (tax TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop table if exists item_store_two");
        DB.execSQL("drop table if exists user_info");
        DB.execSQL("drop table if exists pin_store");
        DB.execSQL("drop table if exists Tid_store");
        DB.execSQL("drop table if exists sold_item");
        DB.execSQL("drop table if exists tax_value");
    }

    public Boolean insertData(String id,String item,String name,String price,String o_price,String amount){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        cv.put("item",item);
        cv.put("name",name);
        cv.put("price",price);
        cv.put("orignal_price",o_price);
        cv.put("amount",amount);
        long result = DB.insert("item_store_two",null,cv);

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    public boolean updateItemStore(String item, String item_name, String amount) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        String whereClause = "item = ? AND name = ?";
        String[] whereArgs = {item.toLowerCase(), item_name.toLowerCase()};

        int result = DB.update("item_store_two", cv, whereClause, whereArgs);
        return result > 0;
    }
    public boolean updateItemStore_two(String id,String item, String item_name,String o_price,String s_price, String amount) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("price", s_price);
        cv.put("orignal_price", o_price);
        cv.put("amount", amount);
        String whereClause = "item = ? AND name = ?";
        String[] whereArgs = {item.toLowerCase(), item_name.toLowerCase()};

        int result = DB.update("item_store_two", cv, whereClause, whereArgs);
        return result > 0;
    }
    public Boolean insert_sold_item(String item_id,String item,String amount,String profit,String year,String month,String day){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id",item_id);
        cv.put("item",item);
        cv.put("amount",amount);
        cv.put("profit",profit);
        cv.put("year",year);
        cv.put("month",month);
        cv.put("day",day);
        long result = DB.insert("sold_item",null,cv);

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertUser(String username,String password,String user_type,String phone_number,String cbe_birr_pin,String user_id){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username",username);
        cv.put("password",password);
        cv.put("user_type",user_type);
        cv.put("phone_number",phone_number);
        cv.put("cbe_birr_pin",cbe_birr_pin);
        cv.put("user_id",user_id);
        long result = DB.insert("user_info",null,cv);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    public Boolean insertPin(String pin){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pin",pin);
        long result = DB.insert("pin_store",null,cv);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    public Boolean insertTid(String Tid){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Tid",Tid);
        long result = DB.insert("Tid_store",null,cv);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    public Boolean add_tax(String selectedItem){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("tax",selectedItem);
        long result = DB.insert("tax_value",null,cv);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    public String SelectFromStore(String item, String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] selectionArgs = {item, name};
        Cursor cursor = DB.rawQuery("SELECT * FROM item_store_two WHERE item = ? AND name = ?", selectionArgs);
        String amount="";
        while (cursor.moveToNext()){
            amount = cursor.getString(5);
        }
        return amount;
    }
    public Cursor SelectFromStore_two(String item, String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] selectionArgs = {item.toLowerCase(), name.toLowerCase()};
        Cursor cursor = DB.rawQuery("SELECT * FROM item_store_two WHERE item = ? AND name = ?", selectionArgs);
        return cursor;
    }

    public Cursor view(String id){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from item_store_two",null);
        return cursor;
    }
    public Cursor view_tax(String id){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from tax_value",null);
        return cursor;
    }
    public Cursor viewHistory(String month) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] selectionArgs = { month };
        Cursor cursor = DB.rawQuery("SELECT * FROM sold_item WHERE month = ?", selectionArgs);
        return cursor;
    }
    public Cursor viewHistory_byDay(String day,String month) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String[] selectionArgs = { day,month };
        Cursor cursor = DB.rawQuery("SELECT * FROM sold_item WHERE day = ? AND month = ?", selectionArgs);
        return cursor;
    }
    public Cursor viewMonth() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM sold_item", null);
        return cursor;
    }
    public Cursor select_item(String id){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from item_store_two",null);
        return cursor;
    }
    public Cursor viewUser(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from user_info",null);
        return cursor;
    }
    public Cursor viewPin(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from pin_store",null);
        return cursor;
    }
    public Cursor viewTid(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from Tid_store",null);
        return cursor;
    }
    public void deleteItem(String item) {

        SQLiteDatabase DB = this.getWritableDatabase();
        DB.delete("item_store_two", "item=?", new String[]{item});
        DB.close();
    }
    public Boolean delete_tax(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from tax_value");
        return true;
    }
    public Boolean Delete_User(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from user_info");
        return true;
    }
    public Boolean Delete_pin(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from pin_store");
        return true;
    }
    public Boolean Delete_Tid(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from Tid_store");
        return true;
    }
    public Boolean Delete_History(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from sold_item");
        return true;
    }
}
