package com.bender.mpdroid.mpdService;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class MpdServer implements MpdServiceAdapterIF
{
    public static final int DEFAULT_MPD_PORT = 6600;
    public static final String OK_RESPONSE = "OK";

    private String version;
    private MpdPlayerAdapterIF mpdPlayer;
    private MpdPlaylistAdapterIF playlist;
    private static final String TAG = MpdServer.class.getSimpleName();
    private Pipe commandPipe;
    private Pipe callbackPipe;

    private static class Pipe
    {
        private BufferedReader reader;
        private BufferedWriter writer;
        private SocketStreamProviderIF streamProvider;

        private Pipe(SocketStreamProviderIF socketStreamProviderIF)
        {
            streamProvider = socketStreamProviderIF;
        }

        public void connect(SocketAddress socketAddress) throws IOException
        {
            streamProvider.connect(socketAddress);
            reader = streamProvider.getBufferedReader();
            writer = streamProvider.getBufferedWriter();
        }

        public String readLine() throws IOException
        {
            return reader.readLine();
        }

        public void write(String command) throws IOException
        {
            writer.write(command);
            writer.newLine();
            writer.flush();
        }

        public void disconnect() throws IOException
        {
            streamProvider.disconnect();
        }

        public boolean isConnected()
        {
            return streamProvider.isConnected();
        }
    }

    private class CallbackPipe implements Runnable
    {
        private SocketAddress address;
        private boolean disconnected;

        public CallbackPipe(SocketAddress theAddress)
        {
            address = theAddress;
        }

        public void run()
        {
            try
            {
                callbackPipe.connect(address);
                String version = callbackPipe.readLine();
                while (!disconnected)
                {
                    callbackPipe.write(MpdCommands.idle + " player");
                    String changedLine = callbackPipe.readLine();
                    String OKLine = callbackPipe.readLine();
                    validateResponse(OKLine);
                    playerUpdated();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Callback method.
     */
    private void playerUpdated()
    {
        Log.v(TAG, "playerUpdated()");
    }

    public MpdServer()
    {
        this(SocketStreamProvider.class);
    }

    /**
     * For unit testing only
     *
     * @param socketStreamProviderIFClass
     */
    MpdServer(Class socketStreamProviderIFClass)
    {
        try
        {
            Constructor constructor = socketStreamProviderIFClass.getConstructor();
            SocketStreamProviderIF socketStreamProviderIF = (SocketStreamProviderIF) constructor.newInstance();
            commandPipe = new Pipe(socketStreamProviderIF);
            callbackPipe = new Pipe(socketStreamProviderIF);
        }
        catch (Exception e)
        {
            throw new ExceptionInInitializerError(e);
        }
        mpdPlayer = new NullPlayerAdapter();
        playlist = new NullPlaylistAdapter();
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
            String line = connect(commandPipe, socketAddress);
            version = line.trim();
            mpdPlayer = new MpdPlayer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    private String connect(Pipe pipe, SocketAddress socketAddress)
            throws IOException
    {
        pipe.connect(socketAddress);
        String line = pipe.readLine();
        line = validateResponse(line);
        return line;
    }

    private String validateResponse(String line) throws IOException
    {
        boolean valid = line != null && line.startsWith(OK_RESPONSE);
        if (!valid)
        {
            throw new IOException("Server returned: " + line);
        }
        return line.substring(OK_RESPONSE.length());
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
            callbackPipe.disconnect();
            commandPipe.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        mpdPlayer = new NullPlayerAdapter();
    }

    public boolean isConnected()
    {
        return commandPipe.isConnected();
    }

    public String getServerVersion()
    {
        return version;
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        return mpdPlayer;
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return playlist;
    }

    private class MpdPlayer implements MpdPlayerAdapterIF
    {
        public PlayStatus getPlayStatus()
        {
            return PlayStatus.Stopped;
        }

        public void next()
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void prev()
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public Integer setVolume(Integer volume)
        {
            return 0;
        }

        public Integer getVolume()
        {
            return 0;
        }

        public Boolean toggleMute()
        {
            return false;
        }

        public void playOrPause()
        {
            try
            {
                commandPipe.write(MpdCommands.play.toString());
                validateResponse(commandPipe.readLine());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.e(TAG, "", e);
            }
        }


        public void stop()
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public MpdSongAdapterIF getCurrentSong()
        {
            return new NullSongAdapter();
        }

        public void addPlayerListener(MpdSongListener listener)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
