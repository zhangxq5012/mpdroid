package com.bender.mpdlib.simulator;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.commands.*;
import com.bender.mpdlib.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
class ServerThread extends Thread
{
    private SocketStreamProviderIF provider;
    private PrintWriter printWriter;
    private BufferedReader simBufferedReader;
    private static int count;
    private Playlist playlist;
    private SimPlayer simPlayer;
    private SubSystemSupport subSystemSupport;
    private boolean forceClose;
    private static final String TAG = "MpdServerThread";

    public ServerThread(SocketStreamProviderIF commandStreamProvider, Playlist playlist, SimPlayer simPlayer, SubSystemSupport subSystemSupport)
    {
        super("Sim-" + count++);
        this.playlist = playlist;
        this.simPlayer = simPlayer;
        this.subSystemSupport = subSystemSupport;
        provider = commandStreamProvider;
        try
        {
            simBufferedReader = provider.getBufferedReader();
            printWriter = provider.getPrintWriter();
        }
        catch (IOException e)
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
            }
            catch (IOException e)
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
            }
            catch (IOException e)
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
        Log.v(TAG, "process(): " + command);
        switch (command)
        {
            case close:
                provider.disconnect();
                forceClose = true;
                break;
            case idle:
                runSimCommand(new IdleSimCommand(printWriter, subSystemSupport));
                break;
            case status:
                runSimCommand(new StatusSimCommand(printWriter, simPlayer, playlist));
                break;
            case play:
                runSimCommand(new PlaySimSimCommand(printWriter, simPlayer));
                break;
            case pause:
                PauseSimCommand pauseSimCommand = new PauseSimCommand(printWriter, simPlayer);
                pauseSimCommand.run();
                break;
            case stop:
                StopSimCommand stopSimCommand = new StopSimCommand(printWriter, simPlayer);
                stopSimCommand.run();
                break;
            case next:
                runSimCommand(new NextSimCommand(printWriter, playlist));
                break;
            case previous:
                runSimCommand(new PreviousSimCommand(printWriter, playlist));
                break;
            case setvol:
                VolumeSimCommand volumeSimCommand = new VolumeSimCommand(printWriter, strings, simPlayer);
                volumeSimCommand.run();
                break;
            case ping:
                printWriter.println(Response.OK.toString());
                break;
            case currentsong:
                runSimCommand(new CurrentSongSimCommand(printWriter, playlist));
                break;
            default:
                printWriter.write(Response.ACK + "[5@0] \"" + commandString + "\" not implemented");
                break;
        }
        Log.v(TAG, command + " DONE");
    }

    private void runSimCommand(SimCommand simCommand) throws IOException
    {
        try
        {
            simCommand.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            printWriter.write(Response.ACK + "[5@0] Unhandled Exception");
        }
    }

    private boolean connected()
    {
        return provider.isConnected() || !forceClose;
    }
}
