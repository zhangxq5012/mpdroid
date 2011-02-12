package com.bender.mpdlib.commands;

import com.bender.mpdlib.MpdBoolean;
import com.bender.mpdlib.Pipe;

import java.io.IOException;


/**
 */
public class RepeatCommand extends StatusCommand<SingleArg<Boolean>>
{
    public RepeatCommand(Pipe commandPipe, Boolean repeat)
    {
        super(commandPipe, new SingleArg<Boolean>(repeat));
    }

    @Override
    protected void executeCommand(SingleArg<Boolean> arg) throws IOException
    {
        pipe.write(MpdCommands.repeat + " " + MpdBoolean.toString(arg.getArg()));
    }
}
