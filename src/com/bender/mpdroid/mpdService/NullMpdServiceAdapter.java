package com.bender.mpdroid.mpdService;

import android.util.Log;

/**
 * A null MpdAdapter. This is used when the adapter can't be initialized.
 */
class NullMpdServiceAdapter implements MpdServiceAdapterIF
{

    private static final String TAG = NullMpdServiceAdapter.class.getSimpleName();

    public void connect(String server, int port, String password)
    {
        Log.e(TAG, "connect(server,port,password) called on NULL object");
    }

    public void connect(String server, int port)
    {
        Log.e(TAG, "connect(server,port) called on NULL object");
    }

    public void connect(String server)
    {
        Log.e(TAG, "connect(server) called on NULL object");
    }

    public void disconnect()
    {
        Log.e(TAG, "disconnect() called on NULL object");
    }

    public boolean isConnected()
    {
        Log.e(TAG, "isConnected() called on NULL object");
        return false;
    }

    public String getServerVersion()
    {
        Log.e(TAG, "getServerVersion() called on NULL object");
        return null;
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        Log.e(TAG, "getPlayer() called on NULL object");
        return new NullPlayerAdapter();
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        Log.e(TAG, "getPlaylist() called on NULL object");
        return new NullPlaylistAdapter();
    }

    public MpdOptionsIF getOptions()
    {
        Log.e(TAG, "getOptions() called on NULL object");
        return new NullMpdOptions();
    }

    public void addConnectionListener(MpdConnectionListenerIF connectionListenerIF)
    {
        Log.e(TAG, "addConnectionListener() called on NULL object");
    }


    private static class NullMpdOptions implements MpdOptionsIF
    {
        public void toggleRepeat()
        {
            Log.e(TAG, "toggleRepeat() called on NULL object");
        }

        public Boolean getRepeat()
        {
            Log.e(TAG, "getRepeat() called on NULL object");
            return false;
        }

        public void addOptionsListener(OptionsListener optionsListener)
        {
            Log.e(TAG, "addOptionsListener() called on NULL object");
        }

        public void toggleRandom()
        {
            Log.e(TAG, "toggleRandom() called on NULL object");
        }

        public Boolean getRandom()
        {
            Log.e(TAG, "getRandom() called on NULL object");
            return false;
        }
    }
}
