package org.wilderkek.bereke.di.data

import android.annotation.SuppressLint
import android.content.Context

class AppPrefsImpl(val context: Context) : AppPrefs {
    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    override var userId: Int
        get() = prefs.getInt(USER_ID, -1)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putInt(USER_ID, value).commit()
        }


    override var userToken: String?
        get() = prefs.getString(USER_TOKEN, null)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(USER_TOKEN, value).commit()
        }


    override var userTown: String
        get() = prefs.getString(USER_TOWN, null) ?: ""
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(USER_TOWN, value).commit()
        }

    override var uniqueDeviceId: String?
        get() = prefs.getString(USER_DEVICE_ID, null)
        @SuppressLint("ApplySharedPref")
        set(value) {
            prefs.edit().putString(USER_DEVICE_ID, value).commit()
        }


    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_TOWN = "user_town"
        const val USER_DEVICE_ID = "user_device"
    }
}