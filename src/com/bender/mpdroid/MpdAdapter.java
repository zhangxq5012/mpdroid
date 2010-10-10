package com.bender.mpdroid;

import android.util.Log;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDResponseException;

import java.lang.reflect.Constructor;
import java.net.UnknownHostException;

/**
 * todo: replace with documentation
 */
public class MpdAdapter implements MpdAdapterIF
{
    private MPD mpdService;
    private static final String TAG = MpdAdapter.class.getSimpleName();
    private static String adapterClassName;

    public static final String MPD_ADAPTER_CLASSNAME_PROPERTY = "MPD_ADAPTER";

    //todo: move to factory class
    static
    {
        adapterClassName = System.getProperty(MPD_ADAPTER_CLASSNAME_PROPERTY, MpdAdapter.class.getName());

    }

    public static MpdAdapterIF createAdapter()
    {
        MpdAdapterIF mpdAdapterIF;
        try
        {
            Class<MpdAdapterIF> adapterClass = (Class<MpdAdapterIF>) Class.forName(adapterClassName);
            Constructor<MpdAdapterIF> constructor = adapterClass.getConstructor();
            constructor.setAccessible(true);
            mpdAdapterIF = constructor.newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
            mpdAdapterIF = new NullMpdAdapter();
        }
        return mpdAdapterIF;
    }

    public MpdAdapter()
    {
        this(null);
    }

    private MpdAdapter(MPD mpdService)
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
            e.printStackTrace();
            Log.e(TAG, "", e);
        } catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    private static class NullMpdAdapter implements MpdAdapterIF
    {
        public void connect(String server, int port, String password)
        {
            Log.e(TAG, "connect(server,port,password) called on NULL object");
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
    }
}
