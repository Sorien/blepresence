package com.sorien.ppbthome

import android.content.Context
import android.content.SharedPreferences

class Config(context: Context) {
    var preferences: SharedPreferences
    var editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    var autoStart: Boolean?
        get() = preferences.getBoolean(KEY_AUTO_START, true)
        set(status) {
            editor.putBoolean(KEY_AUTO_START, status!!)
            editor.commit()
        }

    companion object {
        private const val PREFER_NAME = "settings"
        const val KEY_AUTO_START = "auto_start"
    }
}