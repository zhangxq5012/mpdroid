package com.bender.mpdlib.simulator.commands;

import java.io.PrintWriter;

/**
 */
public abstract class SimCommand
{
    protected final PrintWriter writer;

    protected SimCommand(PrintWriter writer)
    {
        this.writer = writer;
    }

    public abstract void run() throws Exception;
}
