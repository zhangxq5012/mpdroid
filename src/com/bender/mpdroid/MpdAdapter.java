package com.bender.mpdroid;

import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDResponseException;

import java.net.UnknownHostException;

/**
 * todo: replace with documentation
 */
public class MpdAdapter
{
    private MPD mpdService;
    private static final String TAG = MpdAdapter.class.getSimpleName();

    public MpdAdapter()
    {
    }

    MpdAdapter(MPD mpdService)
    {
        this.mpdService = mpdService;
    }

    public void connect(String server, int port, String password)
    {
        try
        {
            if (mpdService != null)
            {
                mpdService = new MPD(server, port, password);
            }
        } catch (UnknownHostException e)
        {
            Log.e(TAG, "", e);
        } catch (MPDConnectionException e)
        {
            Log.e(TAG, "", e);
        }
    }

    public void connect(String server)
    {
        try
        {
            mpdService = new MPD(server);
        } catch (UnknownHostException e)
        {
            Log.e(TAG, "", e);
        } catch (MPDConnectionException e)
        {
            Log.e(TAG, "", e);
        }
    }

    public void disconnect()
    {
        try
        {
            if (mpdService != null)
            {
                mpdService.close();
            }
        } catch (MPDConnectionException e)
        {
            Log.e(TAG, "", e);
        } catch (MPDResponseException e)
        {
            Log.e(TAG, "", e);
        }
        mpdService = null;
    }

    public boolean isConnected()
    {
        if (mpdService != null)
        {
            return mpdService.isConnected();
        }
        return false;
    }

    public void play()
    {
        try
        {
            if (mpdService != null)
            {
                mpdService.getMPDPlayer().play();
            }
        } catch (MPDConnectionException e)
        {
            Log.e(TAG, "", e);
        } catch (MPDPlayerException e)
        {
            Log.e(TAG, "", e);
        }
    }
}
