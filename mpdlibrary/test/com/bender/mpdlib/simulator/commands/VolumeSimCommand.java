package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

/**
 */
public class VolumeSimCommand extends SimCommand
{

    @Override
    public void run(String[] commands)
    {
        if (commands.length == 2)
        {
            Integer volume;
            try
            {
                volume = Integer.parseInt(commands[1].trim());
                simPlayer.setVolume(volume);
                printWriter.println(Response.OK);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                printWriter.println(Response.ACK + " [2@0] {setvol} need an integer");
            }
        } else
        {
            printWriter.println(Response.ACK + " [2@0] {setvol} wrong number of arguments for \"setvol\"");
        }
    }

}
