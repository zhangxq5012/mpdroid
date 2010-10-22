package com.bender.mpdlib.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * todo: replace with documentation
 */
public class Log
{
    public static final String PATTERN = "MM-dd HH:mm:ss.SSS";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

    private enum level
    {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        WTF
    }

    public static void v(String tag, String text)
    {
        print(level.VERBOSE, tag, text);
    }

    private static void print(level verbose, String tag, String text)
    {
        System.out.println(SIMPLE_DATE_FORMAT.format(new Date())
                + "-(" + Thread.currentThread().getName()
                + ")-[" + verbose + "]->" + tag + ": " + text);
    }

    public static void d(String tag, String text)
    {
        print(level.DEBUG, tag, text);
    }

    public static void i(String tag, String text)
    {
        print(level.INFO, tag, text);
    }

    public static void w(String tag, String text)
    {
        print(level.WARN, tag, text);
    }

    public static void e(String tag, String text, Exception e)
    {
        print(level.ERROR, tag, text);
        e.printStackTrace(System.out);
    }

    public static void e(String tag, Exception e)
    {
        e(tag, e.getMessage(), e);
    }

    public static void wtf(String tag, String text)
    {
        print(level.WTF, tag, text);
    }
}
