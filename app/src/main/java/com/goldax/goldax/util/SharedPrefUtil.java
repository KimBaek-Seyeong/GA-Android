package com.goldax.goldax.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.goldax.goldax.LoginActivity;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.data.DataConst;

public class SharedPrefUtil {
    public final String SHARED_PREF = "SHARED_PREF";

    private SharedPreferences mPref;

    private SharedPrefUtil() {
        mPref = ApplicationController.getInstance().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static SharedPrefUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        public static final SharedPrefUtil INSTANCE = new SharedPrefUtil();
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }


    /**
     * 로그아웃 처리
     *
     * @param activity
     */
    public void removeLoginData(Activity activity) {
        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_LOGIN_TOKEN, null);
        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_LOGIN_NAME, null);
        SharedPrefUtil.getInstance().set(DataConst.KEY.SP_USER_INDEX, null);

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish(); // 메인 액티비티 종료
    }

}
