package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.commands.Response;

/**
 */
public abstract class PlayerSimCommand extends SimCommand
{

    public final void run(String[] commands)
    {
        try
        {
            if (commands.length == 1)
            {
                simPlayer.updatePlayStatus(getPlayStatus());
                printWriter.println(Response.OK);
            } else
            {
                runWithArgs(commands);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void runWithArgs(String[] commands)
    {

    }

    protected abstract PlayStatus getPlayStatus();

}
