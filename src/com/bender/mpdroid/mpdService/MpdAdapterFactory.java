package com.bender.mpdroid.mpdService;

import java.lang.reflect.Constructor;

/**
 * Constructs the appropriate #MpdServiceAdapterIF instance based on the system property #MPD_ADAPTER_CLASSNAME_PROPERTY.
 * If the initialization fails then a null object #MpdAdapaterIF is constructed.
 *
 * @see MpdServiceAdapterIF
 */
public class MpdAdapterFactory
{
    private static final String TAG = MpdAdapterFactory.class.getSimpleName();

    public static final String MPD_ADAPTER_CLASSNAME_PROPERTY = "MPD_ADAPTER";

    public static final String DEFAULT_ADAPTER = MpdLibServiceAdapter.class.getName();

    static
    {
        System.setProperty(MPD_ADAPTER_CLASSNAME_PROPERTY, DEFAULT_ADAPTER);
    }

    /**
     * Create a new instance of the #MpdServiceAdapterIF instance.
     *
     * @return new instance or Null object if instantiation fails.
     */
    public static MpdServiceAdapterIF createAdapter()
    {
        MpdServiceAdapterIF mpdServiceAdapterIF;
        try
        {
            @SuppressWarnings({"unchecked"}) Class<MpdServiceAdapterIF> adapterClass = (Class<MpdServiceAdapterIF>) Class.forName(System.getProperty(MPD_ADAPTER_CLASSNAME_PROPERTY));
            Constructor<MpdServiceAdapterIF> constructor = adapterClass.getConstructor();
            constructor.setAccessible(true);
            mpdServiceAdapterIF = constructor.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mpdServiceAdapterIF = new NullMpdServiceAdapter();
        }
        return mpdServiceAdapterIF;
    }

}
