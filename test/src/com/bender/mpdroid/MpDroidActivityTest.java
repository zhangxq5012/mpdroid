package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;

public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity>
{

    public MpDroidActivityTest()
    {
        super("com.bender.mpdroid", MpDroidActivity.class);
    }

    public void testTheTruth()
    {
        assertEquals(true, true);
    }

}
