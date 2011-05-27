package com.bender.mpdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 */
public class SystemSettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);
    }
}