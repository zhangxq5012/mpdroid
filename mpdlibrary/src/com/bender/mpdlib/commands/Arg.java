package com.bender.mpdlib.commands;

/**
 */
public abstract class Arg
{
    private final Object[] args;

    public Arg(Object... args)
    {
        this.args = args;
    }

    public Object[] getArgs()
    {
        return args;
    }

    @Override
    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < args.length; i++)
        {
            stringBuffer.append(args[i]);
            if ((i + 1) < args.length)
            {
                stringBuffer.append(' ');
            }
        }
        return stringBuffer.toString();
    }
}
