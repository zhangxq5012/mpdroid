package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public class Status
{
    public boolean success;
    public String resultString;

    public static Status parse(String line)
    {
        Status status = new Status();
        status.success = line != null && line.startsWith(Response.OK.toString());
        status.resultString = line;
        return status;
    }
}
