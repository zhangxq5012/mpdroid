package com.bender.mpdlib;


/**
 */
public enum Subsystem
{
    database,
    update,
    stored_playlist,
    player,
    mixer,
    output,
    options;

    public static Subsystem parse(String changedLine)
    {
        String[] strings = changedLine.split(":");
        String changed = strings[0].trim();
        if (changed.equals("changed") && strings.length == 2)
        {
            String subsystemString = strings[1].trim();
            for (Subsystem subsystem : values())
            {
                if (subsystemString.equals(subsystem.toString()))
                {
                    return subsystem;
                }
            }
        }
        return null;
    }
}
