package com.creative.routetracker.sharedprefs;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.creative.routetracker.BuildConfig;
import com.creative.routetracker.model.User;
import com.google.gson.Gson;


/**
 * Created by jubayer on 6/6/2017.
 */


public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static Gson GSON = new Gson();
    // Sharedpref file name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private static final String KEY_USER = "user";
    private static final String KEY_NUM_OF_TIME_USER_SET_ALARM = "num_of_time_user_set_alarm";
    private static final String KEY_SET_IS_APP_RUN_FIRST_TIME = "is_app_run_first_time";
    private static final String KEY_EMAIL_CACHE = "key_email_cache";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setEmailCache(String obj) {
        editor = pref.edit();

        editor.putString(KEY_EMAIL_CACHE, obj);

        // commit changes
        editor.commit();
    }
    public String getEmailCache() {
        return pref.getString(KEY_EMAIL_CACHE,"");
    }

    public void setNumberOfTimeUserSetAlarm(int obj) {
        editor = pref.edit();

        editor.putInt(KEY_NUM_OF_TIME_USER_SET_ALARM, obj);

        // commit changes
        editor.commit();
    }
    public int getNumberOfTimeUserSetAlarm() {
        return pref.getInt(KEY_NUM_OF_TIME_USER_SET_ALARM,0);
    }

    public void setIsAppRunFirstTime(boolean obj) {
        editor = pref.edit();

        editor.putBoolean(KEY_SET_IS_APP_RUN_FIRST_TIME, obj);

        // commit changes
        editor.commit();
    }
    public boolean getIsAppRunFirstTime() {
        return pref.getBoolean(KEY_SET_IS_APP_RUN_FIRST_TIME,true);
    }

    public void setUserProfile(User obj) {
        editor = pref.edit();

        editor.putString(KEY_USER, GSON.toJson(obj));

        // commit changes
        editor.commit();
    }

    public void setUserProfile(String obj) {
        editor = pref.edit();

        editor.putString(KEY_USER, obj);

        // commit changes
        editor.commit();
    }

    public User getUserProfile() {

        String gson = pref.getString(KEY_USER, "");
        if (gson.isEmpty()) return null;
        return GSON.fromJson(gson, User.class);
    }

}