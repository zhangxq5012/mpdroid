package com.bender.mpdroid.mpdService;

import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    private Socket mock;
    public static final String HOSTNAME = "hostname";
    private boolean connected;
    private static final int PORT = 6601;

    @Override
    public void setUp() throws Exception
    {
        mock = mock(Socket.class);
        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                connected = true;
                return null;
            }
        }).when(mock).connect((SocketAddress) anyObject());
        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                connected = false;
                return null;
            }
        }).when(mock).close();
        when(mock.isConnected()).thenAnswer(new Answer<Boolean>()
        {
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                return connected;
            }
        });

        mpdServer = new MpdServer(mock);
    }

    public void testConnectWithHostname() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        SocketAddress socketAddress = new InetSocketAddress(HOSTNAME, MpdServer.DEFAULT_MPD_PORT);
        verify(mock).connect(socketAddress);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithHostnameAndPort() throws Exception
    {
        mpdServer.connect(HOSTNAME, PORT);

        SocketAddress socketAddress = new InetSocketAddress(HOSTNAME, PORT);
        verify(mock).connect(socketAddress);
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
}
