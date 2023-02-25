package com.jdream.oneq.user;

import android.content.Context;

public class User {
    private String name;
    private long phone;
    private int pinCode;

    public User() {
    }

    public User(String name, long phone, int pinCode) {
        this.name = name;
        this.phone = phone;
        this.pinCode = pinCode;
    }

    public void createSharedPreference(Context context) {

    }
    public String getName() {
        return name;
    }

    public long getPhone() {
        return phone;
    }

    public int getPinCode() {
        return pinCode;
    }
}
