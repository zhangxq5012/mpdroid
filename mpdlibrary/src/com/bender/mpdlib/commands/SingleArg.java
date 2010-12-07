package com.bender.mpdlib.commands;

/**
 */
public class SingleArg<T> extends Arg
{
    public SingleArg(T arg)
    {
        super(arg);
    }

    @SuppressWarnings("unchecked")
    public T getArg()
    {
        return (T) getArgs()[0];
    }
}
