package com.bender.mpdlib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class Pipe
{
    private BufferedReader reader;
    private BufferedWriter writer;
    private SocketStreamProviderIF streamProvider;

    public Pipe(SocketStreamProviderIF socketStreamProviderIF)
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

    public void write(Object command) throws IOException
    {
        write(command.toString());
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
