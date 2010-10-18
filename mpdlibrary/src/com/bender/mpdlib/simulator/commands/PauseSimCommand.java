package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public class PauseSimCommand extends PlayerSimCommand
{
    public PauseSimCommand(BufferedWriter writer)
    {
        super(writer);
    }

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Paused;
    }
}
