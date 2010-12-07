package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

/**
 */
public class PreviousSimCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        printWriter.println(Response.OK);
        playlist.previous();
        simPlayer.previous();
    }
}
