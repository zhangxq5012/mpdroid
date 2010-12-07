package com.bender.mpdlib.simulator;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.commands.CloseSimCommand;
import com.bender.mpdlib.simulator.commands.CommandResourceIF;
import com.bender.mpdlib.simulator.commands.SimCommand;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 */
class ServerThread extends Thread implements CommandResourceIF
{
    private SocketStreamProviderIF provider;
    private PrintWriter printWriter;
    private BufferedReader simBufferedReader;
    private static int count;
    private Playlist playlist;
    private SimPlayer simPlayer;
    private SubSystemSupport subSystemSupport;
    private static final String TAG = "MpdServerThread";
    private SimCommandFactory simCommandFactory;

    public ServerThread(SocketStreamProviderIF commandStreamProvider, Playlist playlist, SimPlayer simPlayer, SubSystemSupport subSystemSupport)
    {
        super("Sim-" + count++);
        this.playlist = playlist;
        this.simPlayer = simPlayer;
        this.subSystemSupport = subSystemSupport;
        provider = commandStreamProvider;
        simCommandFactory = new SimCommandFactory();
        try
        {
            simBufferedReader = provider.getBufferedReader();
            printWriter = provider.getPrintWriter();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        if (connected())
        {
            printWriter.println(Response.OK + " " + MpdServerSimulator.VERSION);
        }
        while (connected())
        {
            String line = null;
            try
            {
                line = simBufferedReader.readLine();
            } catch (IOException e)
            {
                Log.e(TAG, e);
            }
            if (!connected() || line == null)
            {
                return;
            }
            try
            {
                process(line);
            } catch (IOException e)
            {
                Log.e(TAG, e);
            }
        }
    }

    private void process(String line) throws IOException
    {
        String[] strings = line.split("\\s");
        String commandString = strings[0].trim();
        MpdCommands command = MpdCommands.parse(commandString);
        if (command == null)
        {
            printWriter.println(Response.ACK + " [5@0] {} unknown command \"" + commandString + "\"");
            return;
        }
        Log.v(TAG, "process: " + command);
        try
        {
            SimCommand simCommand = simCommandFactory.create(command);
            if (simCommand == null)
            {
                printWriter.println(Response.ACK + "[5@0] Unimplemented method: " + command);
            } else
            {
                runCommand(strings, simCommand);
            }
        } catch (Exception e)
        {
            Log.e(TAG, e);
            printWriter.println(Response.ACK + "[5@0] Unhandled Exception");
        }
        Log.v(TAG, command + " DONE");
    }

    private void runCommand(String[] strings, SimCommand simCommand)
            throws Exception
    {
        simCommand.execute(strings, this);
    }

    private boolean connected()
    {
        return provider.isConnected() || !CloseSimCommand.isClosed();
    }

    public SocketStreamProviderIF getProvider()
    {
        return provider;
    }

    public PrintWriter getPrintWriter()
    {
        return printWriter;
    }

    public Playlist getPlaylist()
    {
        return playlist;
    }

    public SimPlayer getSimPlayer()
    {
        return simPlayer;
    }

    public SubSystemSupport getSubSystemSupport()
    {
        return subSystemSupport;
    }
}
