package com.bender.mpdroid.mpdService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class MpdServer implements MpdServiceAdapterIF
{
    public static final int DEFAULT_MPD_PORT = 6600;

    private Socket socket;
    private boolean connected;
    private InputStream inputStream;
    private OutputStream outputStream;

    public MpdServer()
    {
        this(null);
    }

    /**
     * For unit testing only
     *
     * @param socket
     */
    MpdServer(Socket socket)
    {
        this.socket = socket;
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
            if (socket == null)
            {
                socket = new Socket();
            }
            socket.connect(socketAddress);
            connected = socket.isConnected();
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
}
