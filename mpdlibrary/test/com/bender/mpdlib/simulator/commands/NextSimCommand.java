package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

/**
 */
public class NextSimCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        playlist.next();
        simPlayer.next();
        printWriter.println(Response.OK);
    }
}
