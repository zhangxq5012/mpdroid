package com.bender.mpdlib;

import com.bender.mpdlib.commands.Command;
import com.bender.mpdlib.util.Log;

/**
 * todo: replace with documentation
 */
class CommandRunner
{
    public static final String TAG = "CommandRunner";

    public static <K, T> T runCommand(Command<K, T> command)
    {
        try
        {
            return callCommand(command);
        }
        catch (Exception e)
        {
            Log.e(TAG, e);
        }
        return null;
    }

    public static <K, T> T callCommand(Command<K, T> command) throws Exception
    {
        Log.i(TAG, "callCommand: " + command.getClass().getSimpleName());
        return command.call();
    }
}
