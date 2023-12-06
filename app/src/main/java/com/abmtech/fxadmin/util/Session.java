package com.abmtech.fxadmin.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.abmtech.fxadmin.ui.LoginActivity;


public class Session {
    private static final String PREF_NAME = "Rapidine_pref2";
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String Mobile = "mobile";
    private static final String Email = "email";
    private static final String UserId = "user_id";
    private static final String User_name = "user_name";
    private static final String isPrime = "isPrime";

    private static final String userImage = "userImage";
    private static final String type = "type";
    private static final String myInviteCode = "myInviteCode";
    private static final String expireDate = "expireDate";
    private static final String upiId = "upiId";
    private static final String ifscCode = "ifscCode";
    private static final String accountNumber = "accountNumber";

    private final Context _context;
    public SharedPreferences Rapidine_pref;
    private final SharedPreferences.Editor editor;

    public Session(Context context) {
        this._context = context;
        Rapidine_pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = Rapidine_pref.edit();
        editor.apply();
    }


    public void setMobile(String mobile) {
        editor.putString(Mobile, mobile);
        editor.apply();
    }


    public void setEmail(String email) {
        editor.putString(Email, email);
        editor.apply();
    }

    public void setType(String email) {
        editor.putString(type, email);
        editor.apply();
    }

    public void setIsPrime(boolean email) {
        editor.putBoolean(isPrime, email);
        editor.apply();
    }

    public void setUpiId(String s) {
        editor.putString(upiId, s);
        editor.apply();
    }

    public void setExpireDate(String s) {
        editor.putString(expireDate, s);
        editor.apply();
    }

    public void setIfscCode(String s) {
        editor.putString(ifscCode, s);
        editor.apply();
    }

    public void setAccountNumber(String s) {
        editor.putString(accountNumber, s);
        editor.apply();
    }

    public void setUserImage(String email) {
        editor.putString(userImage, email);
        editor.apply();
    }

    public void setMyInviteCode(String email) {
        editor.putString(myInviteCode, email);
        editor.apply();
    }


    public String getType() {
        return Rapidine_pref.getString(type, "");
    }

    public String getExpireDate() {
        return Rapidine_pref.getString(expireDate, "");
    }

    public String getUserImage() {
        return Rapidine_pref.getString(userImage, "");
    }

    public String getMyInviteCode() {
        return Rapidine_pref.getString(myInviteCode, "");
    }

    public boolean getIsPrime() {
        return Rapidine_pref.getBoolean(isPrime, false);
    }

    public String getMobile() {
        return Rapidine_pref.getString(Mobile, "");
    }

    public String getUserName() {
        return Rapidine_pref.getString(User_name, "");
    }

    public void setUserName(String user_name) {
        editor.putString(User_name, user_name);
        this.editor.apply();
    }
    public String getUserId() {
        return Rapidine_pref.getString(UserId, "");
    }

    public void setUserId(String userId) {
        editor.putString(UserId, userId);
        this.editor.apply();
    }

    public String getEmail() {
        return Rapidine_pref.getString(Email, "");
    }


    public void setCategory(String cat) {
        editor.putString("cat", cat);
        this.editor.apply();
    }
    public String getCategory() {
        return Rapidine_pref.getString("cat", "");
    }


    public void setGender(String cat) {
        editor.putString("gender", cat);
        this.editor.apply();
    }
    public String getGender() {
        return Rapidine_pref.getString("gender", "");
    }
    public String getAccountNumber() {
        return Rapidine_pref.getString(accountNumber, "");
    }
    public String getIfscCode() {
        return Rapidine_pref.getString(ifscCode, "");
    }
    public String getUpiId() {
        return Rapidine_pref.getString(upiId, "");
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGEDIN, isLoggedIn);
        editor.apply();
    }

    public void logout() {
        _context.startActivity(new Intent(_context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        editor.clear();
        editor.apply();
    }


    public boolean isLoggedIn() {
        return Rapidine_pref.getBoolean(IS_LOGGEDIN, false);
    }

}