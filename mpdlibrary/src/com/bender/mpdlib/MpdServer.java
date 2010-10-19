package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo: replace with documentation
 */
public class MpdServer
{
    public static final int DEFAULT_MPD_PORT = 6600;

    private String version;
    private Pipe commandPipe;
    private Pipe callbackPipe;
    private CallbackPipe callbackThread;

    private PlayStatus playState;
    private PlayStatusListener myListener = new NullPlayStatusListener();
    private Integer volume = 0;
    private VolumeListener volumeListener = new NullVolumeListener();
    private AtomicInteger ignoreVolumeUpdate = new AtomicInteger(0);

    public MpdServer()
    {
        this(new SocketStreamProvider(), new SocketStreamProvider());
    }

    /**
     * For testing only.
     *
     * @param socketStreamProviderIF stream provider to use for both pipes
     */
    public MpdServer(SocketStreamProviderIF socketStreamProviderIF)
    {
        this(socketStreamProviderIF, socketStreamProviderIF);
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
                    PlayStatus newPlayStatus = PlayStatus.parse(statusTuple.second());
                    boolean changed = !newPlayStatus.equals(playState);
                    if (changed)
                    {
                        playState = newPlayStatus;
                        myListener.playStatusChanged();
                    }
                    break;
                case volume:
                    Integer newVolume = Integer.parseInt(statusTuple.second());
                    if (!newVolume.equals(volume))
                    {
                        volume = newVolume;
                        if (ignoreVolumeUpdate.decrementAndGet() < 0)
                        {
                            ignoreVolumeUpdate.incrementAndGet();
                            volumeListener.volumeChanged(volume);
                        }
                    }
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

    public void pause()
    {
        runCommand(new PauseCommand(commandPipe));
    }

    public void addPlayStatusListener(PlayStatusListener listener)
    {
        myListener = listener;
    }

    public void previous()
    {
        runCommand(new PreviousCommand(commandPipe));
    }

    public Integer getVolume()
    {
        return volume;
    }

    public void setVolume(Integer volume)
    {
        ignoreVolumeUpdate.incrementAndGet();
        runCommand(new SetVolumeCommand(commandPipe, volume));
    }

    public void addVolumeListener(VolumeListener listener)
    {
        volumeListener = listener;
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
                    Result<List<Subsystem>> idleResult = runCommand(new IdleCommand(callbackPipe, new Subsystem[]{}));
                    if (disconnected)
                    {
                        return;
                    }
                    if (idleResult.status.success)
                    {
                        List<Subsystem> result = idleResult.result;
                        for (Subsystem subsystem : result)
                        {
                            switch (subsystem)
                            {
                                case player:
                                    playerUpdated();
                                    break;
                                case mixer:
                                    volumeUpdated();
                                    break;
                            }
                        }
                    }
                }
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
        }

        private void volumeUpdated()
        {
            Result<List<StatusTuple>> listResult = runCommand(new GetStatusCommand(callbackPipe));
            processStatuses(listResult.result);
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

    private class NullPlayStatusListener implements PlayStatusListener
    {
        public void playStatusChanged()
        {
        }
    }

    private class NullVolumeListener implements VolumeListener
    {
        public void volumeChanged(Integer volume)
        {
        }
    }
}
