package com.bender.mpdroid.mpdService;

import java.awt.image.ImagingOpException;
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

    private boolean connected;
    private SocketStreamProviderIF socketProviderIF;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String version;

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
            connected = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String validateResponse(String line)
    {
        boolean valid = line.startsWith(OK_RESPONSE);
        if (!valid)
        {
            throw new ImagingOpException("Server returned: " + line);
        }
        return line.substring(OK_RESPONSE.length());
    }

    private String readLine() throws IOException
    {
        return bufferedReader.readLine();
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
    }

    public boolean isConnected()
    {
        return connected;
    }

    public String getServerVersion()
    {
        return null;
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        return null;
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return null;
    }

    public String getVersion()
    {
        return version;
    }
}
