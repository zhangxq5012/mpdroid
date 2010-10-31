package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 */
public class MpdServer
{
    public static final int DEFAULT_MPD_PORT = 6600;

    private String version;
    private Pipe commandPipe;
    private Pipe callbackPipe;
    private CallbackThread callbackThread;
    private MpdPlayer player;
    private static final String TAG = MpdServer.class.getSimpleName();

    /**
     * Normal use constructor.
     */
    public MpdServer()
    {
        this(new SocketStreamProvider(), new SocketStreamProvider());
    }

    /**
     * For testing only
     *
     * @param socketStreamProvider  stream provider for command pipe
     * @param socketStreamProvider1 stream provider for callback pipe
     */
    public MpdServer(SocketStreamProviderIF socketStreamProvider, SocketStreamProviderIF socketStreamProvider1)
    {
        commandPipe = new Pipe(socketStreamProvider);
        callbackPipe = new Pipe(socketStreamProvider1);
    }

    public void connect(String server, int port, String password)
    {
        boolean authenticate = password != null;
        if (authenticate)
        {
            throw new IllegalArgumentException("authentication not supported");
        }
        SocketAddress socketAddress = new InetSocketAddress(server, port);
        try
        {
            ConnectCommand connectCommand = new ConnectCommand(commandPipe, socketAddress);

            Result<String> result = CommandRunner.callCommand(connectCommand);
            version = result.result;

            GetStatusCommand statusCommand = new GetStatusCommand(commandPipe);
            Result<List<StatusTuple>> listResult = CommandRunner.runCommand(statusCommand);
            processStatuses(listResult.result);


            callbackThread = new CallbackThread(this, socketAddress, callbackPipe);
            callbackThread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void processStatuses(List<StatusTuple> result)
    {
        getPlayer();
        player.processStatus(result);
    }


    public void connect(String server, int port)
    {
        connect(server, port, null);
    }

    public void connect(String server)
    {
        connect(server, DEFAULT_MPD_PORT);
    }

    public void disconnect()
    {
        try
        {
            player.disconnect();
            player = null;
            CommandRunner.runCommand(new DisconnectCommand(commandPipe));
            commandPipe.disconnect();
            callbackThread.interrupt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isConnected()
    {
        return commandPipe.isConnected();
    }

    public String getServerVersion()
    {
        return version;
    }

    public synchronized Player getPlayer()
    {
        if (player == null)
        {
            player = new MpdPlayer(commandPipe);
        }
        return player;
    }


}
