package com.bender.mpdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class Pipe
{
    private BufferedReader reader;
    private PrintWriter writer;
    private SocketStreamProviderIF streamProvider;

    public Pipe(SocketStreamProviderIF socketStreamProviderIF)
    {
        streamProvider = socketStreamProviderIF;
    }

    public void connect(SocketAddress socketAddress) throws IOException
    {
        streamProvider.connect(socketAddress);
        reader = streamProvider.getBufferedReader();
        writer = new PrintWriter(streamProvider.getBufferedWriter(), true);
    }

    public String readLine() throws IOException
    {
        return reader.readLine();
    }

    public void write(Object command)
    {
        write(command.toString());
    }

    public void write(String command)
    {
        writer.println(command);
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
