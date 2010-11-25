package com.bender.mpdlib.commands;

/**
 */
public class SingleArg<T> extends Arg
{
    public SingleArg(T arg)
    {
        super(arg);
    }

    public T getArg()
    {
        //noinspection unchecked
        return (T) getArgs()[0];
    }
}
