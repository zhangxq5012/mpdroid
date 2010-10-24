package com.bender.mpdlib.commands;

import com.bender.mpdlib.MpdStatus;

/**
 */
public class StatusTuple extends Tuple<MpdStatus, String>
{
    public StatusTuple(MpdStatus val1, String val2)
    {
        super(val1, val2);
    }

    public MpdStatus getStatus()
    {
        return first();
    }

    public String getValue()
    {
        return second();
    }
}
