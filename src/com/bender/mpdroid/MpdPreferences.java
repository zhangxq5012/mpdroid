package com.bender.mpdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Manages the preferences for mpd droid.
 */
public class MpdPreferences {
    private static final int DEFAULT_MPD_PORT = 6600;
    private static final String SERVER_KEY = "server";
    private static final String AUTO_CONNECT_KEY = "auto_connect";
    private SharedPreferences sharedPreferences;
    private static final String WIFI_ONLY = "wifi_only";

    public MpdPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getServer() {
        return sharedPreferences.getString(SERVER_KEY, "");
    }

    public boolean usePort() {
        return sharedPreferences.getBoolean("use_port", false);
    }

    public int getPort() {
        int port;
        if (usePort()) {
            port = Integer.parseInt(sharedPreferences.getString("port", "6600"));
        } else {
            port = getDefaultPort();
        }
        return port;
    }

    public boolean useAuthentication() {
        return sharedPreferences.getBoolean("use_authentication", false);
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    public boolean autoConnect() {
        boolean autoConnect = sharedPreferences.getBoolean(AUTO_CONNECT_KEY, true);
        boolean serverDefined = sharedPreferences.contains(SERVER_KEY);
        autoConnect = autoConnect && serverDefined;
        return autoConnect;
    }

    public boolean autoConnectWithWifiOnly() {
        return sharedPreferences.getBoolean(WIFI_ONLY, false);
    }

    public boolean firstRun() {
        return sharedPreferences.contains(SERVER_KEY);
    }

    public int getDefaultPort() {
        return DEFAULT_MPD_PORT;
    }
}
