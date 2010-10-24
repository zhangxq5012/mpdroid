package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

import java.io.PrintWriter;

/**
 */
public class PauseSimCommand extends PlayerSimCommand
{
    public PauseSimCommand(PrintWriter writer, SimPlayer simPlayer)
    {
        super(writer, simPlayer);
    }

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Paused;
    }
}
