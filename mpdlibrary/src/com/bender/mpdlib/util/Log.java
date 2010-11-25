package com.bender.mpdlib.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logging utility class for mpdlibrary.
 *
 * To change the log strategy use #Log.setLogStrategy.  The default strategy is to log everything to system out.
 */
public class Log
{
    public static final String PATTERN = "MM-dd HH:mm:ss.SSS";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(PATTERN);

    private static LogStrategy logStrategy;

    static
    {
        logStrategy = new SysOutLogStrategy();
    }

    /**
     * Change the log strategy.
     *
     * @param logStrategy new log strategy
     */
    public static void setLogStrategy(LogStrategy logStrategy)
    {
        Log.logStrategy = logStrategy;
    }

    public static void setLevel(LogStrategy.level lvl)
    {
        logStrategy.setLevel(lvl);
    }

    public static void v(String tag, String text)
    {
        logStrategy.v(tag, text);
    }


    public static void d(String tag, String text)
    {
        logStrategy.d(tag, text);
    }

    public static void i(String tag, String text)
    {
        logStrategy.i(tag, text);
    }

    public static void w(String tag, String text)
    {
        logStrategy.w(tag, text);
    }

    public static void e(String tag, String text, Exception e)
    {
        logStrategy.e(tag, text, e);
    }

    public static void e(String tag, Exception e)
    {
        logStrategy.e(tag, e);
    }

    public static void wtf(String tag, String text)
    {
        logStrategy.wtf(tag, text);
    }

    private static class SysOutLogStrategy implements LogStrategy
    {

        private void print(level lvl, String tag, String text)
        {
            if (isEnabled(lvl))
            {
                System.out.println(SIMPLE_DATE_FORMAT.format(new Date())
                        + "-[" + lvl + "]-(" + Thread.currentThread().getName()
                        + ")->" + tag + ": " + text);
            }
        }

        private boolean isEnabled(level lvl)
        {
            synchronized (this)
            {
                return currentLevel.isEnabled(lvl);
            }
        }

        private level currentLevel = level.VER;

        public void setLevel(level lvl)
        {
            synchronized (this)
            {
                currentLevel = lvl;
            }
        }

        public void v(String tag, String text)
        {
            print(level.VER, tag, text);
        }

        public void d(String tag, String text)
        {
            print(level.DBG, tag, text);
        }

        public void i(String tag, String text)
        {
            print(level.INF, tag, text);
        }

        public void w(String tag, String text)
        {
            print(level.WRN, tag, text);
        }

        public void e(String tag, String text, Exception e)
        {
            print(level.ERR, tag, text);
            e.printStackTrace(System.out);
        }

        public void e(String tag, Exception e)
        {
            e(tag, e.getMessage(), e);
        }

        public void wtf(String tag, String text)
        {
            print(level.WTF, tag, text);
        }
    }
}
