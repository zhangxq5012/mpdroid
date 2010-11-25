package com.bender.mpdroid.mpdService;

import android.util.Log;
import com.bender.mpdlib.util.LogStrategy;

/**
 * Implementation of #LogStrategy that uses Android's logging framework.
 *
 */
public class AndroidLogStrategy implements LogStrategy
{
    public void v(String tag, String text)
    {
        Log.v(tag, text);
    }

    public void d(String tag, String text)
    {
        Log.d(tag, text);
    }

    public void i(String tag, String text)
    {
        Log.i(tag, text);
    }

    public void w(String tag, String text)
    {
        Log.w(tag, text);
    }

    public void e(String tag, String text, Exception e)
    {
        Log.e(tag, text, e);
    }

    public void e(String tag, Exception e)
    {
        e(tag, e.getMessage(), e);
    }

    public void wtf(String tag, String text)
    {
        Log.wtf(tag, text);
    }

    /**
     * Not used! Android has it's own level filtering mechanism.
     *
     * @param lvl level
     */
    public void setLevel(level lvl)
    {
        //no-op
    }
}
