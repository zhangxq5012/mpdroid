package com.bender.mpdlib.commands;

/**
 */
public class Status
{
    private boolean success;
    private String resultString;

    public boolean isSuccessful()
    {
        return success;
    }

    public String getResultString()
    {
        return resultString;
    }

    public static Status parse(String line)
    {
        Status status = new Status();
        status.success = line != null && line.startsWith(Response.OK.toString());
        status.resultString = line;
        return status;
    }
}
