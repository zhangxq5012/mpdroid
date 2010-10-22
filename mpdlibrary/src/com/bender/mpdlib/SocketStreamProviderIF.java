package com.bender.mpdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public interface SocketStreamProviderIF
{
    void connect(SocketAddress socketAddress) throws IOException;

    BufferedReader getBufferedReader() throws IOException;

    PrintWriter getPrintWriter() throws IOException;

    void disconnect() throws IOException;

    boolean isConnected();
}
