package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public class NextSimCommand extends SimCommand
{
    public NextSimCommand(BufferedWriter simBufferedWriter)
    {
        super(simBufferedWriter);
    }

    @Override
    public void run() throws Exception
    {
        Playlist.next();
        writer.write(Response.OK.toString());
    }
}
