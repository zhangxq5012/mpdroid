package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * todo: replace with documentation
 */
public abstract class Command<T> implements Callable<T>
{
    protected final Pipe pipe;

    public Command(Pipe pipe)
    {
        this.pipe = pipe;
    }

    public T call() throws Exception
    {
        executeCommand();
        return readResult();
    }

    public abstract void executeCommand() throws IOException;

    public abstract T readResult() throws IOException;
}
