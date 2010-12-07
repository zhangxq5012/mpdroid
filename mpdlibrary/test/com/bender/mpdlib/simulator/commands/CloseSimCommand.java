package com.bender.mpdlib.simulator.commands;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * todo: replace with documentation
 */
public class CloseSimCommand extends SimCommand
{
    private static AtomicBoolean forceClose = new AtomicBoolean(false);

    @Override
    public void run(String[] commands) throws Exception
    {
        socketStreamProviderIF.disconnect();
        forceClose.set(true);
    }

    public static boolean isClosed()
    {
        return forceClose.get();
    }
}
