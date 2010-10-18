package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public class PlaySimSimCommand extends PlayerSimCommand
{
    public PlaySimSimCommand(BufferedWriter writer)
    {
        super(writer);
    }

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Playing;
    }
}
