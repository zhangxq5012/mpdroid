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
    private CallbackThread callbackThread;
    private Integer volume = 0;
    private VolumeListener volumeListener = new NullVolumeListener();
    private AtomicInteger ignoreVolumeUpdate = new AtomicInteger(0);
    private MpdPlayer player;
    private static final String TAG = MpdServer.class.getSimpleName();

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

            Result<String> result = CommandRunner.callCommand(connectCommand);
            version = result.result;

            GetStatusCommand statusCommand = new GetStatusCommand(commandPipe);
            Result<List<StatusTuple>> listResult = CommandRunner.runCommand(statusCommand);
            processStatuses(listResult.result);


            callbackThread = new CallbackThread(socketAddress);
            callbackThread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void processStatuses(List<StatusTuple> result)
    {
        getPlayer();
        player.processStatus(result);
        for (StatusTuple statusTuple : result)
        {
            switch (statusTuple.getStatus())
            {
                case volume:
                    Integer newVolume = Integer.parseInt(statusTuple.getValue());
                    if (!newVolume.equals(volume))
                    {
                        volume = newVolume;
                        if (ignoreVolumeUpdate.decrementAndGet() < 0)
                        {
                            ignoreVolumeUpdate.incrementAndGet();
                            volumeListener.volumeChanged(volume);
                        }
                    }
                    break;
            }
        }
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


    public Integer getVolume()
    {
        return volume;
    }

    public void setVolume(Integer volume)
    {
        ignoreVolumeUpdate.incrementAndGet();
        CommandRunner.runCommand(new SetVolumeCommand(commandPipe, volume));
    }

    public void addVolumeListener(VolumeListener listener)
    {
        volumeListener = listener;
    }

    private class CallbackThread extends Thread
    {
        private SocketAddress address;
        private volatile boolean disconnected;

        public CallbackThread(SocketAddress theAddress)
        {
            super("MpdServer.CallbackThread");
            address = theAddress;
        }

        public void run()
        {
            try
            {
                Result<String> connectResult = CommandRunner.runCommand(new ConnectCommand(callbackPipe, address));
                if (!connectResult.status.isSuccessful())
                {
                    System.out.println(TAG + "connect unsuccessful: " + connectResult.status.getResultString());
                    return;
                }
                while (!disconnected)
                {
                    Result<List<Subsystem>> idleResult = CommandRunner.runCommand(new IdleCommand(callbackPipe));
                    if (disconnected)
                    {
                        System.out.println("CallbackThread disconnected");
                        return;
                    }
                    if (idleResult.status.isSuccessful())
                    {
                        System.out.println("CallbackThread: idle successful");
                        List<Subsystem> result = idleResult.result;
                        if (result.contains(Subsystem.mixer) || result.contains(Subsystem.player))
                        {
                            getAndProcessStatus();
                        }
                    }
                    else
                    {
                        System.out.println(TAG + "idle unsuccessful: " + idleResult.status.getResultString());
                    }
                }
            }
            catch (Exception e)
            {
                if (!disconnected)
                {
                    System.out.println("CallbackThread ERROR" + e.getMessage());
                    e.printStackTrace(System.out);
                }
            }
            System.out.println(TAG + "CallbackThread DONE");
        }

        private void getAndProcessStatus()
        {
            Result<List<StatusTuple>> listResult = CommandRunner.runCommand(new GetStatusCommand(callbackPipe));
            System.out.println("CallbackThread.getAndProcessStatus " + listResult.result.size());
            processStatuses(listResult.result);
        }

        @Override
        public void interrupt()
        {
            disconnected = true;
            try
            {
//                CommandRunner.runCommand(new DisconnectCommand(callbackPipe));
                callbackPipe.write(MpdCommands.close);
                callbackPipe.disconnect();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }


    private class NullVolumeListener implements VolumeListener
    {
        public void volumeChanged(Integer volume)
        {
        }
    }
}
