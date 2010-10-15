package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.bender.mpdroid.mpdService.MpdAdapterFactory;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;
import com.bender.mpdroid.mpdService.StubbedMpdServiceAdapater;

public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity>
{
    static
    {
        System.setProperty(MpdAdapterFactory.MPD_ADAPTER_CLASSNAME_PROPERTY, StubbedMpdServiceAdapater.class.getName());
    }

    private MpDroidActivity activity;

    public MpDroidActivityTest()
    {
        super("com.bender.mpdroid", MpDroidActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();
    }

    public void testConstructor()
    {
        assertNotNull(activity);
    }

    public void testConnnect() throws Exception
    {
        // press connect button
        final View connectButton = activity.findViewById(R.id.connect);
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                connectButton.requestFocus();
                connectButton.performClick();
            }
        });

        MpdServiceAdapterIF mpdServiceAdapterIF = activity.getMpdServiceAdapterIF();
        assertEquals(true, mpdServiceAdapterIF instanceof StubbedMpdServiceAdapater);
        StubbedMpdServiceAdapater stubbedMpdAdapater = (StubbedMpdServiceAdapater) mpdServiceAdapterIF;
        // make sure connect is called
        assertEquals(1, stubbedMpdAdapater.connectCount);
        assertEquals(1, stubbedMpdAdapater.disconnectCount);

    }
}
