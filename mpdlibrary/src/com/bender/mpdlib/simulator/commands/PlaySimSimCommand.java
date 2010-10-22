package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class PlaySimSimCommand extends PlayerSimCommand
{
    public PlaySimSimCommand(PrintWriter writer)
    {
        super(writer);
    }

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Playing;
    }
}
