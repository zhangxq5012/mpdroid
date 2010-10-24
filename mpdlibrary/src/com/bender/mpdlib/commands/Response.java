package com.bender.mpdlib.commands;

/**
 */
public enum Response
{
    OK,
    ACK;

    public static boolean isResponseLine(String line)
    {
        return line != null && (line.startsWith(Response.OK.toString()) || line.startsWith(Response.ACK.toString()));
    }
}
