package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public enum Response
{
    OK,
    ACK;

    public static boolean isResponseLine(String line)
    {
        return line.startsWith(Response.OK.toString()) || line.startsWith(Response.ACK.toString());
    }
}
