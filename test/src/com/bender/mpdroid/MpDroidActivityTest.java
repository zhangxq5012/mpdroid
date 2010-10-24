package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;

public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity>
{
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
    }
}
