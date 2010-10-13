package com.bender.mpdroid.mpdService;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class MpdServer implements MpdServiceAdapterIF
{
    public static final int DEFAULT_MPD_PORT = 6600;
    public static final String OK_RESPONSE = "OK";

    private SocketStreamProviderIF socketProviderIF;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String version;
    private MpdPlayerAdapterIF mpdPlayer;
    private MpdPlaylistAdapterIF playlist;
    private static final String TAG = MpdServer.class.getSimpleName();

    public MpdServer()
    {
        this(new SocketStreamProvider());
    }

    /**
     * For unit testing only
     *
     * @param socketStreamProviderIF i/o
     */
    MpdServer(SocketStreamProviderIF socketStreamProviderIF)
    {
        socketProviderIF = socketStreamProviderIF;
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
            socketProviderIF.connect(socketAddress);
            bufferedReader = socketProviderIF.getBufferedReader();
            bufferedWriter = socketProviderIF.getBufferedWriter();
            String line = readLine();
            line = validateResponse(line);
            version = line.trim();
            mpdPlayer = new MpdPlayer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
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

    private String readLine() throws IOException
    {
        return bufferedReader.readLine();
    }

    private void executeCommand(String command)
            throws IOException
    {
        bufferedWriter.write(command);
        bufferedWriter.newLine();
        bufferedWriter.flush();
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
            socketProviderIF.disconnect();
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
        return socketProviderIF.isConnected();
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

        public PlayStatus playOrPause()
        {
            try
            {
                executeCommand(MpdCommands.play.toString());
                validateResponse(readLine());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.e(TAG, "", e);
            }
            return PlayStatus.Stopped;
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
