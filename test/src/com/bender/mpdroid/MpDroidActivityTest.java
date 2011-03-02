package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import com.bender.mpdroid.mpdService.StubbedMpdService;

public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity>
{
    private MpDroidActivity activity;
    private StubbedMpdService mpdService;

    public MpDroidActivityTest()
    {
        super("com.bender.mpdroid", MpDroidActivity.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();
        mpdService = new StubbedMpdService();
        activity.setMpdService(mpdService);
    }

    public void testConstructor()
    {
        assertNotNull(activity);
    }

    public void testConnnect() throws Exception
    {
        assertEquals(false, mpdService.isConnected());
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
        getInstrumentation().waitForIdleSync();
        Thread.sleep(1000);
        assertEquals(true, mpdService.isConnected());
    }
}
