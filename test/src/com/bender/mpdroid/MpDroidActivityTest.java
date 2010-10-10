package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity>
{

    private MpDroidActivity activity;static
{
    System.setProperty(MpdAdapter.MPD_ADAPTER_CLASSNAME_PROPERTY, StubbedMpdActivity.class.getName());
}

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

        MpdAdapterIF mpdAdapterIF = activity.getMpdAdapterIF();
        assertEquals(true, mpdAdapterIF instanceof StubbedMpdActivity);
        StubbedMpdActivity stubbedMpdActivity = (StubbedMpdActivity) mpdAdapterIF;
        // make sure connect is called
        assertEquals(1, stubbedMpdActivity.connectCount);
        assertEquals(1, stubbedMpdActivity.disconnectCount);

    }
}
