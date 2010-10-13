package com.bender.mpdroid.mpdService;

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
        socket = new Socket();
        socket.connect(socketAddress);
    }

    public BufferedReader getBufferedReader() throws IOException
    {
        if (socket == null)
        {
            throw new IllegalStateException("connect() must be called first");
        }
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public BufferedWriter getBufferedWriter() throws IOException
    {
        if (socket == null)
        {
            throw new IllegalStateException("connect() must be called first");
        }
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
}
