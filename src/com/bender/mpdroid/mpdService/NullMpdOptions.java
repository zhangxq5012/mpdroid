package com.bender.mpdroid.mpdService;

import android.util.Log;

public class NullMpdOptions implements MpdOptionsIF
{
    private static final String TAG = NullMpdOptions.class.getSimpleName();

    public void toggleRepeat()
    {
        Log.e(TAG, "toggleRepeat() called on NULL Object");
    }

    public Boolean getRepeat()
    {
        Log.e(TAG, "getRepeat() called on NULL Object");
        return false;
    }

    public void addOptionsListener(OptionsListener optionsListener)
    {
        Log.e(TAG, "addOptionsListener() called on NULL Object");
    }

    public void toggleRandom()
    {
        Log.e(TAG, "toggleRandom() called on NULL Object");
    }

    public Boolean getRandom()
    {
        Log.e(TAG,"getRandom() called on NULL Object");
        return false;
    }
}
