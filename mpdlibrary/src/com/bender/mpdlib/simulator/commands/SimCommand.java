package com.bender.mpdlib.simulator.commands;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public abstract class SimCommand
{
    protected final BufferedWriter writer;

    protected SimCommand(BufferedWriter writer)
    {
        this.writer = writer;
    }

    protected abstract void run() throws Exception;
}
