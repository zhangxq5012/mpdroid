package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public enum MpdCommands
{
    idle,
    play,
    next,
    pause,
    stop,
    previous,
    status,
    close,
    ping,
    crossfade,
    currentsong,
    random,
    repeat,
    setvol;

    public static MpdCommands parse(String value)
    {
        for (MpdCommands mpdCommands : values())
        {
            if (mpdCommands.toString().equals(value))
            {
                return mpdCommands;
            }
        }
        return null;
    }
}
