package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.simulator.SimPlayer;

import java.io.PrintWriter;

/**
 */
public class StopSimCommand extends PlayerSimCommand
{
    public StopSimCommand(PrintWriter writer, SimPlayer simPlayer)
    {
        super(writer, simPlayer);
    }

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Stopped;
    }
}
