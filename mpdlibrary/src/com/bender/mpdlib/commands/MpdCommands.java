package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public enum MpdCommands
{
    idle,
    play,
    crossfade,
    currentsong,
    next,
    pause,
    stop,
    ping,
    previous,
    random,
    repeat,
    status,
    close,
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
