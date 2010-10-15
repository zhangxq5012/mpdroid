package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * todo: replace with documentation
 */
public class MpdServer
{
    public static final int DEFAULT_MPD_PORT = 6600;
    public static final String OK_RESPONSE = Response.OK.toString();

    private String version;
    private static final String TAG = MpdServer.class.getSimpleName();
    private Pipe commandPipe;
    private Pipe callbackPipe;
    private CallbackPipe callbackThread;

    private PlayStatus playState;

    public MpdServer()
    {
        this(new SocketStreamProvider(), new SocketStreamProvider());
    }

    /**
     * For unit testing only
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

            Result<String> result = runCommand(connectCommand);
            version = result.result;

            GetStatusCommand statusCommand = new GetStatusCommand(commandPipe);
            Result<List<StatusTuple>> listResult = runCommand(statusCommand);
            processStatuses(listResult.result);


            callbackThread = new CallbackPipe(socketAddress);
            callbackThread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processStatuses(List<StatusTuple> result)
    {
        for (StatusTuple statusTuple : result)
        {
            switch (statusTuple.first())
            {
                case state:
                    playState = PlayStatus.parse(statusTuple.second());
                    break;
            }
        }
    }

    private static <T> T runCommand(Command<T> command)
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
            runCommand(new DisconnectCommand(commandPipe));
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

    public void play()
    {
        try
        {
            runCommand(new PlayCommand(commandPipe));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Callback method.
     */
    private void playerUpdated()
    {
        Result<List<StatusTuple>> listResult = runCommand(new GetStatusCommand(callbackPipe));
        processStatuses(listResult.result);
        // todo: notify listener
    }

    public PlayStatus getPlayStatus()
    {
        return playState;
    }

    public void stop()
    {
        runCommand(new StopCommand(commandPipe));
    }

    public void next()
    {
        runCommand(new NextCommand(commandPipe));
    }

    private class CallbackPipe extends Thread
    {
        private SocketAddress address;
        private volatile boolean disconnected;

        public CallbackPipe(SocketAddress theAddress)
        {
            super("MpdServer.CallbackPipe");
            address = theAddress;
        }

        public void run()
        {
            try
            {
                Result<String> connectResult = runCommand(new ConnectCommand(callbackPipe, address));
                if (!connectResult.status.success)
                {
                    return;
                }
                while (!disconnected)
                {
                    Result<String[]> idleResult = runCommand(new IdleCommand(callbackPipe, new String[]{"player"}));
                    if (idleResult.status.success)
                    {
                        playerUpdated();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void interrupt()
        {
            disconnected = true;
            try
            {
                callbackPipe.disconnect();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }
}
