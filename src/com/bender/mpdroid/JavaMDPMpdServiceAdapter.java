package com.bender.mpdroid;

import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDResponseException;

import java.net.UnknownHostException;

/**
 * This is an mpd adapter that uses the JavaMPD library to talk to the mpd server.
 *
 * @see org.bff.javampd.MPD
 */
class JavaMDPMpdServiceAdapter implements MpdServiceAdapterIF
{
    private MPD mpdService;
    private static final String TAG = JavaMDPMpdServiceAdapter.class.getSimpleName();
    private boolean muted;

    public JavaMDPMpdServiceAdapter()
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
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }


    public void connect(String server, int port)
    {
        try
        {
            mpdService = new MPD(server, port);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDConnectionException e)
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
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDConnectionException e)
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
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDResponseException e)
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

    public MpdPlayerAdapterIF getPlayer()
    {
        if (mpdService != null)
        {
            return new JavaMPDPlayerAdapter(mpdService.getMPDPlayer());
        }
        else
        {
            return new NullPlayerAdapter();
        }
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
