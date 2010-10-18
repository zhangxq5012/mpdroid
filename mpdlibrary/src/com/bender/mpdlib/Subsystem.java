package com.bender.mpdlib;

import java.util.StringTokenizer;

/**
 * todo: replace with documentation
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
        StringTokenizer stringTokenizer = new StringTokenizer(changedLine, ":");
        String changed = stringTokenizer.nextToken();
        if (changed.equals("changed") && stringTokenizer.hasMoreTokens())
        {
            String subsystemString = stringTokenizer.nextToken().trim();
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
