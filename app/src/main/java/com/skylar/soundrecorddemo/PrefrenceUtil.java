package com.skylar.soundrecorddemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PrefrenceUtil{

    private static final String PREF_HIGH_QUALITY = "pref_high_quality";

    public static void setPrefHighQuality(Context context, boolean enable){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_HIGH_QUALITY,enable);
        editor.commit();
    }

    public static boolean getPrefHighQuality(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enable = sharedPreferences.getBoolean(PREF_HIGH_QUALITY, false);
        return enable;
    }

}
