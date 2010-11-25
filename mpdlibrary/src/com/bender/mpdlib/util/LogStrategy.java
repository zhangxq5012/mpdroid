package com.bender.mpdlib.util;

/**
 * Provides a logging implementation for mpdlibrary.
 */
public interface LogStrategy
{
    /**
     * Log verbose message.
     *
     * @param tag tag
     * @param text message
     */
    void v(String tag, String text);

    /**
     * Log debug message.
     *
     * @param tag tag
     * @param text message
     */
    void d(String tag, String text);

    /**
     * Log informational message.
     *
     * @param tag tag
     * @param text message
     */
    void i(String tag, String text);

    /**
     * Log warning message.
     *
     * @param tag tag
     * @param text message
     */
    void w(String tag, String text);

    /**
     * Log error message with exception.
     *
     * @param tag tag
     * @param text message
     * @param e exception
     */
    void e(String tag, String text, Exception e);

    /**
     * Log error message with exception.
     *
     * @param tag tag
     * @param e exception
     */
    void e(String tag, Exception e);

    /**
     * Log wtf error message message.
     *
     * @param tag tag
     * @param text message
     */
    void wtf(String tag, String text);

    /**
     * Set the log level.  Only print log levels lvl and above.
     *
     * @param lvl level
     */
    void setLevel(level lvl);

    /**
     * The Logging Level.  Use #level.isEnabled to determine if the level is enabled.
     */
    public static enum level
    {
        VER(0x03F),
        DBG(0x01F),
        INF(0x00F),
        WRN(0x007),
        ERR(0x003),
        WTF(0x001);

        private final int bitMask;

        private level(int bitMask)
        {
            this.bitMask = bitMask;
        }

        /**
         * Is this level enabled based on current logging level of #lvl?
         *
         * @param lvl current logging level
         * @return is this level enabled?
         */
        public boolean isEnabled(level lvl)
        {
            return (bitMask | lvl.bitMask) == bitMask;
        }
    }
}
