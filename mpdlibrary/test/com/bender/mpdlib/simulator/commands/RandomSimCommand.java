package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdBoolean;
import com.bender.mpdlib.commands.Response;

/**
 */
public class RandomSimCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        if (commands.length == 2)
        {
            try
            {
                boolean repeat = MpdBoolean.parseString(commands[1]);
                optionsReg.setRandom(repeat);
                printWriter.println(Response.OK);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                printWriter.println(Response.ACK + "[200] {repeat} needs an integer");
            }
        } else
        {
            printWriter.println(Response.ACK + "[200] {repeat} wrong number of arguments for \"repeat\".");
        }
    }
}
