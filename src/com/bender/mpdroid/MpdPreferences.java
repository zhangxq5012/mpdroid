package com.bender.mpdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MpdPreferences
{
    private SharedPreferences sharedPreferences;

    public MpdPreferences(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getServer()
    {
        return sharedPreferences.getString("server", "");
    }

    public boolean usePort()
    {
        return sharedPreferences.getBoolean("use_port", false);
    }

    public int getPort()
    {
        int port;
        if (usePort())
        {
            port = Integer.parseInt(sharedPreferences.getString("port", "6600"));
        } else
        {
            port = getDefaultPort();
        }
        return port;
    }

    public boolean useAuthentication()
    {
        return sharedPreferences.getBoolean("use_authentication", false);
    }

    public String getPassword()
    {
        return sharedPreferences.getString("password", "");
    }

    public int getDefaultPort()
    {
        return 6600;
    }
}
