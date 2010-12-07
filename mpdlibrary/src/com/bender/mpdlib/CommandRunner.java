package com.bender.mpdlib;

import com.bender.mpdlib.commands.Arg;
import com.bender.mpdlib.commands.Command;
import com.bender.mpdlib.util.Log;

/**
 */
class CommandRunner
{
    public static final String TAG = CommandRunner.class.getSimpleName();

    public static <K extends Arg, T> T runCommand(Command<K, T> command)
    {
        try
        {
            return callCommand(command);
        } catch (Exception e)
        {
            Log.e(TAG, e);
        }
        return null;
    }

    public static <K extends Arg, T> T callCommand(Command<K, T> command) throws Exception
    {
        String debugLine = "callCommand: " + command.toString();
        Log.i(TAG, debugLine);
        return command.call();
    }
}
