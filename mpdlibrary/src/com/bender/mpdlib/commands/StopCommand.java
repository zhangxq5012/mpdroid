package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

/**
 */
public class StopCommand extends BasicCommand
{
    public StopCommand(Pipe pipe)
    {
        super(pipe, MpdCommands.stop);
    }

}
