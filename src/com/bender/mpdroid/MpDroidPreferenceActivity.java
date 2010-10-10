package com.bender.mpdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MpDroidPreferenceActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.server_settings);
    }

}
