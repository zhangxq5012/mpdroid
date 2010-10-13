package com.bender.mpdroid.mpdService;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    public static final String HOSTNAME = "hostname";
    private static final int PORT = 6601;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private SocketStreamProviderIF socketStreamProviderIF;
    public static final String VERSION = "MPD 0.15.0";

    @Override
    public void setUp() throws Exception
    {
        socketStreamProviderIF = mock(SocketStreamProvider.class);
        bufferedReader = mock(BufferedReader.class);
        when(socketStreamProviderIF.getBufferedReader()).thenReturn(bufferedReader);
        bufferedWriter = mock(BufferedWriter.class);
        when(socketStreamProviderIF.getBufferedWriter()).thenReturn(bufferedWriter);
        mpdServer = new MpdServer(socketStreamProviderIF);
        setServerResult(VERSION);
    }

    public void testConnectWithHostname() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        SocketAddress socketAddress = new InetSocketAddress(HOSTNAME, MpdServer.DEFAULT_MPD_PORT);
        verify(socketStreamProviderIF).connect(socketAddress);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithHostnameAndPort() throws Exception
    {
        mpdServer.connect(HOSTNAME, PORT);

        SocketAddress socketAddress = new InetSocketAddress(HOSTNAME, PORT);
        verify(socketStreamProviderIF).connect(socketAddress);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithAuthenticationUnsupported() throws Exception
    {
        boolean unsupported = false;
        try
        {
            mpdServer.connect(HOSTNAME, PORT, "password");
        }
        catch (IllegalArgumentException e)
        {
            unsupported = true;
        }
        assertEquals(true, unsupported);
    }

    public void testVersion() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        String version = mpdServer.getServerVersion();
        assertEquals(VERSION, version);
    }

    private void setServerResult(String version)
            throws IOException
    {
        when(bufferedReader.readLine()).thenReturn("OK " + version);
    }
}
