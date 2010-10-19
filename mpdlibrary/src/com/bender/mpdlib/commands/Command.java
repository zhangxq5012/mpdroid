package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * todo: replace with documentation
 */
public abstract class Command<V, T> implements Callable<T>
{
    protected final Pipe pipe;
    private V arg;

    public Command(Pipe pipe)
    {
        this(pipe, null);
    }

    public Command(Pipe pipe, V arg)
    {
        this.pipe = pipe;
        this.arg = arg;
    }

    public T call() throws Exception
    {
        synchronized (pipe)
        {
            executeCommand(arg);
            return readResult();
        }
    }

    protected abstract void executeCommand(V arg) throws IOException;

    protected abstract T readResult() throws IOException;
}
