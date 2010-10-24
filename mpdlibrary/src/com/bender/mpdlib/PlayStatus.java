package com.bender.mpdlib;

/**
 */
public enum PlayStatus
{
    Playing("play"),
    Paused("pause"),
    Stopped("stop");

    public final String serverString;

    PlayStatus(String s)
    {
        serverString = s;
    }

    public static PlayStatus parse(String second)
    {
        for (PlayStatus playStatus : values())
        {
            if (playStatus.serverString.equals(second))
            {
                return playStatus;
            }
        }
        return null;
    }
}
