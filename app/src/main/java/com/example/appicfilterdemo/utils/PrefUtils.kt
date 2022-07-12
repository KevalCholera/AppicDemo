package com.example.appicfilterdemo.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.appicfilterdemo.R

class PrefUtils(context: Context) {

    private lateinit var prefs: SharedPreferences
//    private var mContext: Context = context

    init {
        getPrefs(context)
    }

    fun getPrefs(context: Context): SharedPreferences {
        prefs = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
        return context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
    }

    fun removePref(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * Store integer value
     * */
    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    /**
     * Retrieve integer value
     * */
    fun getInt(key: String): Int {
        return prefs.getInt(key, 0)
    }


    /**
     * Store string value
     * */
    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }





    /**
     * Retrieve string value
     * */
    fun getString(key: String): String? {
        return prefs.getString(key, "")
    }

    /**
     * Store boolean value
     * */
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    /**
     * Retrieve boolean value
     * */
    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    fun hasPref(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * Clear current session
     * */
    fun logout() {
        prefs.edit().clear().apply()
        prefs.edit().commit()
    }
}