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
    private static String adapterClassName;

    public static final String MPD_ADAPTER_CLASSNAME_PROPERTY = "MPD_ADAPTER";

    static
    {
        adapterClassName = System.getProperty(MPD_ADAPTER_CLASSNAME_PROPERTY, MpdLibServiceAdapter.class.getName());
//        adapterClassName = System.getProperty(MPD_ADAPTER_CLASSNAME_PROPERTY, SimulatedMpdServerAdapter.class.getName());
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
            @SuppressWarnings({"unchecked"}) Class<MpdServiceAdapterIF> adapterClass = (Class<MpdServiceAdapterIF>) Class.forName(adapterClassName);
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
