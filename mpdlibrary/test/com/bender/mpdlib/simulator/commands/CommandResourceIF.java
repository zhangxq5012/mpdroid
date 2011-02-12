package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.simulator.OptionsReg;
import com.bender.mpdlib.simulator.SimPlayer;
import com.bender.mpdlib.simulator.SubSystemSupport;
import com.bender.mpdlib.simulator.library.Playlist;

import java.io.PrintWriter;

/**
 */
public interface CommandResourceIF
{
    PrintWriter getPrintWriter();

    Playlist getPlaylist();

    SimPlayer getSimPlayer();

    SubSystemSupport getSubSystemSupport();

    SocketStreamProviderIF getProvider();

    OptionsReg getOptionsReg();
}
