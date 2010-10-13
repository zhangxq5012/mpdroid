package com.bender.mpdroid.mpdService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class SimulatedMpdServer implements SocketStreamProviderIF
{
    private boolean connected;
    public static final String VERSION = "OK MPD 0.15.0";
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public SimulatedMpdServer(BufferedReader reader, BufferedWriter writer)
    {
        bufferedReader = reader;
        bufferedWriter = writer;
    }

    public void connect(SocketAddress socketAddress) throws IOException
    {
    }

    public BufferedReader getBufferedReader() throws IOException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BufferedWriter getBufferedWriter() throws IOException
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
