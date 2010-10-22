package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class NextSimCommand extends SimCommand
{
    private Playlist playlist;

    public NextSimCommand(PrintWriter simBufferedWriter, Playlist playlist)
    {
        super(simBufferedWriter);
        this.playlist = playlist;
    }

    @Override
    public void run() throws Exception
    {
        playlist.next();
        writer.println(Response.OK);
    }
}
