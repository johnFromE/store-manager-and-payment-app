package com.example.qr;

public class HelperClass {
    String username;
    String password;
    String phone_number;
    String username_type;
    String user_cbe_pin;
    String user_id;

    public HelperClass(String username, String password, String phone_number, String username_type, String user_cbe_pin, String user_id) {
        this.username = username;
        this.password = password;
        this.phone_number = phone_number;
        this.username_type = username_type;
        this.user_cbe_pin = user_cbe_pin;
        this.user_id = user_id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername_type() {
        return username_type;
    }

    public void setUsername_type(String username_type) {
        this.username_type = username_type;
    }

    public String getUser_cbe_pin() {
        return user_cbe_pin;
    }

    public void setUser_cbe_pin(String user_cbe_pin) {
        this.user_cbe_pin = user_cbe_pin;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
