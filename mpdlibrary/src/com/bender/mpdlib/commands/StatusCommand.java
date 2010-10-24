package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public abstract class StatusCommand<V> extends Command<V, Status>
{
    public StatusCommand(Pipe pipe)
    {
        super(pipe);
    }

    public StatusCommand(Pipe pipe, V arg)
    {
        super(pipe, arg);
    }

    @Override
    public final Status readResult() throws IOException
    {
        String line = pipe.readLine();
        Status status = Status.parse(line);
        return status;
    }
}
