package com.bender.mpdroid.mpdService;

import android.util.Log;

/**
 * todo: replace with documentation
 */
class NullSongAdapter implements MpdSongAdapterIF
{
    private static final String TAG = NullSongAdapter.class.getSimpleName();

    public String getSongName()
    {
        Log.e(TAG, "getSongName() called on NULL object");
        return "";
    }
}
