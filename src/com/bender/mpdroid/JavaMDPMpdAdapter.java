package com.bender.mpdroid;

import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDResponseException;

import java.net.UnknownHostException;

/**
 * This is an mpd adapter that uses the JavaMPD library to talk to the mpd server.
 *
 * @see org.bff.javampd.MPD
 */
public class JavaMDPMpdAdapter implements MpdAdapterIF
{
    private MPD mpdService;
    private static final String TAG = JavaMDPMpdAdapter.class.getSimpleName();

    public JavaMDPMpdAdapter()
    {
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
            e.printStackTrace();
            Log.e(TAG, "", e);
        } catch (MPDConnectionException e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
            Log.e(TAG, "", e);
        } catch (MPDConnectionException e)
        {
            e.printStackTrace();
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
            e.printStackTrace();
            Log.e(TAG, "", e);
        } catch (MPDResponseException e)
        {
            e.printStackTrace();
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

    public String getServerVersion()
    {
        String version = null;
        if (mpdService != null)
        {
            version = mpdService.getVersion();
        }
        return version;
    }

    public void playOrPause()
    {
        try
        {
            if (mpdService != null)
            {
                mpdService.getMPDPlayer().play();
            }
        } catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        } catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

}
