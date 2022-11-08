package com.sorien.ppbthome;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static final String PREFER_NAME = "settings";
    public static final String KEY_AUTO_START = "auto_start";

    public Config(Context context) {
        preferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setAutoStart(Boolean status){
        editor.putBoolean(KEY_AUTO_START, status);
        editor.commit();
    }
    public Boolean getAutoStart(){
        return preferences.getBoolean(KEY_AUTO_START, true);
    }
}
