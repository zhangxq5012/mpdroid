package com.bender.mpdlib.commands;

import com.bender.mpdlib.MpdBoolean;
import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class RandomCommand extends StatusCommand<SingleArg<MpdBoolean>>
{
    public RandomCommand(Pipe commandPipe, MpdBoolean mpdBoolean)
    {
        super(commandPipe,new SingleArg<MpdBoolean>(mpdBoolean));
    }

    @Override
    protected void executeCommand(SingleArg<MpdBoolean> arg) throws IOException
    {
        pipe.write(MpdCommands.random + " " + arg.getArg());
    }
}
