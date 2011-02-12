package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.simulator.OptionsReg;
import com.bender.mpdlib.simulator.SimPlayer;
import com.bender.mpdlib.simulator.SubSystemSupport;
import com.bender.mpdlib.simulator.library.Playlist;

import java.io.PrintWriter;

/**
 */
public abstract class SimCommand
{

    protected final String TAG = getClass().getSimpleName();
    protected PrintWriter printWriter;
    protected Playlist playlist;
    protected SimPlayer simPlayer;
    protected SubSystemSupport subSystemSupport;
    protected SocketStreamProviderIF socketStreamProviderIF;
    protected OptionsReg optionsReg;

    protected SimCommand()
    {
    }

    public final void execute(String[] commands, CommandResourceIF resourceIF) throws Exception
    {
        printWriter = resourceIF.getPrintWriter();
        playlist = resourceIF.getPlaylist();
        simPlayer = resourceIF.getSimPlayer();
        optionsReg = resourceIF.getOptionsReg();
        subSystemSupport = resourceIF.getSubSystemSupport();
        socketStreamProviderIF = resourceIF.getProvider();
        run(commands);
    }

    public abstract void run(String[] commands) throws Exception;
}
