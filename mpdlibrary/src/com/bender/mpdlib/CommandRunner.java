package com.bender.mpdlib;

import com.bender.mpdlib.commands.Command;

/**
 * todo: replace with documentation
 */
class CommandRunner
{
    public static <K, T> T runCommand(Command<K, T> command)
    {
        try
        {
            return command.call();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
