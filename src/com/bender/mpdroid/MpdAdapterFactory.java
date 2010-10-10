package com.bender.mpdroid;

import android.util.Log;

import java.lang.reflect.Constructor;

/**
 * Constructs the appropriate #MpdAdapterIF instance based on the system property #MPD_ADAPTER_CLASSNAME_PROPERTY.
 * If the initialization fails then a null object #MpdAdapaterIF is constructed.
 *
 * @see com.bender.mpdroid.MpdAdapterIF
 */
public class MpdAdapterFactory
{
    private static final String TAG = MpdAdapterFactory.class.getSimpleName();
    private static String adapterClassName;

    public static final String MPD_ADAPTER_CLASSNAME_PROPERTY = "MPD_ADAPTER";

    static
    {
        adapterClassName = System.getProperty(MPD_ADAPTER_CLASSNAME_PROPERTY, JavaMDPMpdAdapter.class.getName());
    }

    public static MpdAdapterIF createAdapter()
    {
        MpdAdapterIF mpdAdapterIF;
        try
        {
            @SuppressWarnings({"unchecked"}) Class<MpdAdapterIF> adapterClass = (Class<MpdAdapterIF>) Class.forName(adapterClassName);
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

    /**
     * A null MpdAdapter. This is used when the adapter can't be initialized.
     */
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
