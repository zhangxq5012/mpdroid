package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.SimBoolean;

/**
 * todo: replace with documentation
 */
public class RepeatSimCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        if (commands.length == 2)
        {
            try
            {
                boolean repeat = SimBoolean.parseString(commands[1]);
                simPlayer.setRepeat(repeat);
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
