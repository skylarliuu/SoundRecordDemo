package com.skylar.soundrecorddemo.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.skylar.soundrecorddemo.BuildConfig;
import com.skylar.soundrecorddemo.PrefrenceUtil;
import com.skylar.soundrecorddemo.R;
import com.skylar.soundrecorddemo.activity.SettingActivity;

/**
 * Created by Administrator on 2017/9/20.
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        Log.i("test","setting fragment");
        CheckBoxPreference checkBox = (CheckBoxPreference) findPreference(getString(R.string.pref_high_quality_key));
        checkBox.setChecked(PrefrenceUtil.getPrefHighQuality(getActivity()));
        checkBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                PrefrenceUtil.setPrefHighQuality(getActivity(),(boolean)o);
                return true;
            }
        });

        Preference about = findPreference(getString(R.string.pref_about_key));
        about.setSummary(getString(R.string.pref_about_desc, BuildConfig.VERSION_NAME));
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LicenseFragment fragment = new LicenseFragment();
                fragment.show(((SettingActivity)getActivity()).getSupportFragmentManager().beginTransaction(),"license_dialog");
                return true;
            }
        });

    }
}
