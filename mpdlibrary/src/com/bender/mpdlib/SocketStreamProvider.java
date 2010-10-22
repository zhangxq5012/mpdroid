package com.bender.mpdlib;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class SocketStreamProvider implements SocketStreamProviderIF
{
    private Socket socket;

    public void connect(SocketAddress socketAddress) throws IOException
    {
        if (socket != null)
        {
            throw new IllegalStateException("connect() called twice");
        }
        socket = new Socket();
        try
        {
            socket.connect(socketAddress);
        }
        catch (IOException e)
        {
            socket = null;
            throw e;
        }
    }

    public boolean isConnected()
    {
        return socket != null && socket.isConnected();
    }

    public BufferedReader getBufferedReader() throws IOException
    {
        if (socket == null)
        {
            throw new IllegalStateException("connect() must be called first");
        }
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    public PrintWriter getPrintWriter() throws IOException
    {
        if (socket == null)
        {
            throw new IllegalStateException("connect() must be called first");
        }
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }

    public void disconnect() throws IOException
    {
        if (socket == null)
        {
            throw new IllegalStateException("connect() must be called first");
        }
        socket.close();
        socket = null;
    }
}
