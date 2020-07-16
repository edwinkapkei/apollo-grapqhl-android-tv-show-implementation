package com.edwinkapkei.tvshows;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String TV_SHOWS = "TV_SHOWS";

    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_EMAIL = "KEY_EMAIL";
    private static final String KEY_LOGGED_IN = "KEY_LOGGED_IN";

    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private static SessionManager sessionManager;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(TV_SHOWS, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    public SessionManager() {

    }

    public void setUser(String id, String name, String email) {
        editor.putString(KEY_USER, id);
        if (!name.isEmpty())
            editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        return sessionManager.checkLogin();
    }

    private boolean checkLogin() {
        return preferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void logout(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(TV_SHOWS, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserId(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        return sessionManager.getUserIdValue();
    }

    private String getUserIdValue() {
        return preferences.getString(KEY_USER, "");
    }

    public static String getUserName(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        return sessionManager.getUserNameValue();
    }

    private String getUserNameValue() {
        return preferences.getString(KEY_NAME, "");
    }

    public static String getUserEmail(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }

        return sessionManager.getUserIdValue();
    }

    private String getUserEmailValue() {
        return preferences.getString(KEY_EMAIL, "0");
    }
}
