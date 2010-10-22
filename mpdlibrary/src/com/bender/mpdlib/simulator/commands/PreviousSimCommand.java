package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class PreviousSimCommand extends SimCommand
{
    public PreviousSimCommand(PrintWriter simBufferedWriter)
    {
        super(simBufferedWriter);
    }

    @Override
    public void run() throws Exception
    {
        Playlist.previous();
        writer.println(Response.OK);
    }
}
