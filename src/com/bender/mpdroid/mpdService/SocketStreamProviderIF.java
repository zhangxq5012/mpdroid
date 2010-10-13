package com.bender.mpdroid.mpdService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public interface SocketStreamProviderIF
{
    void connect(SocketAddress socketAddress) throws IOException;

    BufferedReader getBufferedReader() throws IOException;

    BufferedWriter getBufferedWriter() throws IOException;
}
