package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

/**
 */
public class PauseCommand extends BasicCommand
{
    public PauseCommand(Pipe pipe)
    {
        super(pipe, MpdCommands.pause);
    }
}
