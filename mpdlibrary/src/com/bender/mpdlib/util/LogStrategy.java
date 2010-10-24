package com.bender.mpdlib.util;

/**
 */
public interface LogStrategy
{
    void v(String tag, String text);

    void d(String tag, String text);

    void i(String tag, String text);

    void w(String tag, String text);

    void e(String tag, String text, Exception e);

    void e(String tag, Exception e);

    void wtf(String tag, String text);

    void setLevel(level lvl);

    public static enum level
    {
        VER(0x03F),
        DBG(0x01F),
        INF(0x00F),
        WRN(0x007),
        ERR(0x003),
        WTF(0x001);

        private final int bitMask;

        level(int bitMask)
        {
            this.bitMask = bitMask;
        }

        public boolean isEnabled(level lvl)
        {
            return (bitMask | lvl.bitMask) == bitMask;
        }
    }
}
