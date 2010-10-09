package com.bender.mpdroid;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.bender.mpdroid.MpDroidActivityTest \
 * com.bender.mpdroid.tests/android.test.InstrumentationTestRunner
 */
public class MpDroidActivityTest extends ActivityInstrumentationTestCase2<MpDroidActivity> {

    public MpDroidActivityTest() {
        super("com.bender.mpdroid", MpDroidActivity.class);
    }

}
