package com.bender.mpdroid;

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


    private class NullPlaylistAdapter implements MpdPlaylistAdapterIF
    {
        public MpdSongAdapterIF getCurrentSong()
        {
            return new NullPlaylistAdapter.NullSongAdapter();
        }

        private class NullSongAdapter implements MpdSongAdapterIF
        {
        }
    }
}
