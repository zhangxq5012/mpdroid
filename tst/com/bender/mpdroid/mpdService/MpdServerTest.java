package com.bender.mpdroid.mpdService;

import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    public static final String HOSTNAME = "hostname";
    private static final int PORT = 6601;
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    public static final String VERSION = "MPD 0.15.0";
    private Queue<String> commandQueue;

    @Override
    public void setUp() throws Exception
    {
        bufferedReader = mock(BufferedReader.class);
        bufferedWriter = mock(BufferedWriter.class);
        commandQueue = new LinkedList<String>();
        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                commandQueue.offer((String) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(bufferedWriter).write(anyString());
        mpdServer = new MpdServer(MySocketStreamProviderIF.class);
        setServerResult(VERSION);
    }

    public void testConnectWithHostname() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithHostnameAndPort() throws Exception
    {
        mpdServer.connect(HOSTNAME, PORT);
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

    public void testPlay() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        MpdPlayerAdapterIF player = mpdServer.getPlayer();

        setServerResult(MpdServer.OK_RESPONSE);
        player.playOrPause();

        assertEquals(1, commandQueue.size());
        assertEquals(MpdCommands.play.toString(), commandQueue.remove());
    }

    private void setServerResult(String version)
            throws IOException
    {
        when(bufferedReader.readLine()).thenReturn("OK " + version);
    }

    public static class MySocketStreamProviderIF implements SocketStreamProviderIF
    {
        private boolean connected;

        public MySocketStreamProviderIF()
        {
        }

        public void connect(SocketAddress socketAddress) throws IOException
        {
            connected = true;
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            return bufferedReader;
        }

        public BufferedWriter getBufferedWriter() throws IOException
        {
            return bufferedWriter;
        }

        public void disconnect() throws IOException
        {
            connected = false;
        }

        public boolean isConnected()
        {
            return connected;
        }
    }
}
