package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.commands.Response;

/**
 */
public abstract class PlayerSimCommand extends SimCommand
{

    public void run(String[] commands)
    {
        try
        {
            simPlayer.updatePlayStatus(getPlayStatus());
            printWriter.println(Response.OK);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected abstract PlayStatus getPlayStatus();

}
