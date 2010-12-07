package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;

/**
 */
public class PlaySimSimCommand extends PlayerSimCommand
{

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Playing;
    }
}
