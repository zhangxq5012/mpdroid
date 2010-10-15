package com.bender.mpdlib;

import com.bender.mpdlib.commands.MpdCommands;
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
    public static final String VERSION = "MPD 0.15.0";
    private CommandStreamProvider commandStreamProvider;
    private CallbackStreamProvider callbackStreamProvider;

    @Override
    public void setUp() throws Exception
    {
        commandStreamProvider = new CommandStreamProvider();
        callbackStreamProvider = new CallbackStreamProvider();
        mpdServer = new MpdServer(commandStreamProvider, callbackStreamProvider);
        commandStreamProvider.setServerResult(VERSION);
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

        commandStreamProvider.setServerResult(MpdServer.OK_RESPONSE);
        mpdServer.play();

        Queue<String> commandQueue = commandStreamProvider.commandQueue;
        assertEquals(1, commandQueue.size());
        assertEquals(MpdCommands.play.toString(), commandQueue.remove());
    }

    public void testDisconnect() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.setServerResult(MpdServer.OK_RESPONSE);
        mpdServer.disconnect();

        Queue<String> commandQueue = commandStreamProvider.commandQueue;
        assertEquals(1, commandQueue.size());
        assertEquals(MpdCommands.close.toString(), commandQueue.remove());
    }


    public class CommandStreamProvider implements SocketStreamProviderIF
    {
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private Queue<String> commandQueue;

        private boolean connected;

        public CommandStreamProvider() throws IOException
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
        }

        private void setServerResult(String version)
                throws IOException
        {
            when(commandStreamProvider.bufferedReader.readLine()).thenReturn("OK " + version);
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

    private class CallbackStreamProvider implements SocketStreamProviderIF
    {
        private boolean connected;

        public void connect(SocketAddress socketAddress) throws IOException
        {
            connected = true;
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            return mock(BufferedReader.class);
        }

        public BufferedWriter getBufferedWriter() throws IOException
        {
            BufferedWriter mock = mock(BufferedWriter.class);
            doAnswer(new Answer()
            {
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable
                {
                    synchronized (this)
                    {
                        wait();
                    }
                    return null;
                }
            });
            return mock;
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
