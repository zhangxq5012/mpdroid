package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

/**
 */
public class StopSimCommand extends PlayerSimCommand
{

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Stopped;
    }
}
