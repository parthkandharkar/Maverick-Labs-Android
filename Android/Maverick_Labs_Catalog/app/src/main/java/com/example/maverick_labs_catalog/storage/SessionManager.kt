package com.example.maverick_labs_catalog.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.maverick_labs_catalog.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID =  "user_id"
        const val USER_SUPERUSER = "user_superuser"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }


    fun saveUserId(id : Int) {
        val editor = prefs.edit()
        editor.putInt(USER_ID,id)
        editor.apply()
    }

    fun fetchUserId() : Int {
        return prefs.getInt(USER_ID, 0)
    }

    fun savesuperuser(is_superuser : Boolean)  {
        val editor = prefs.edit()
        editor.putBoolean(USER_SUPERUSER,is_superuser)
        editor.apply()
    }


    fun fetchsuperuser() : Boolean {
        return prefs.getBoolean(USER_SUPERUSER,false)
    }


    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}