package com.bender.mpdlib;

import com.bender.mpdlib.commands.MpdCommands;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;

import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
class MockCallbackStreamProvider implements SocketStreamProviderIF
{
    private boolean connected;
    private BufferedReader reader;
    private Queue<String> responseQueue = new LinkedList<String>();

    public void connect(SocketAddress socketAddress) throws IOException
    {
        connected = true;
    }

    public BufferedReader getBufferedReader() throws IOException
    {
        reader = mock(BufferedReader.class);
        when(reader.readLine()).thenAnswer(new Answer<String>()
        {
            public String answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                return responseQueue.remove();
            }
        });
        return reader;
    }

    public PrintWriter getPrintWriter() throws IOException
    {
        PrintWriter mock = mock(PrintWriter.class);
        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                if (invocationOnMock.getArguments()[0].equals(MpdCommands.idle.toString()))
                {
                    synchronized (MockCallbackStreamProvider.this)
                    {
                        MockCallbackStreamProvider.this.wait();
                    }
                }
                return null;
            }
        }).when(mock).println(anyObject());
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

    public void changeEvent()
    {
        synchronized (this)
        {
            notifyAll();
        }
    }

    public void appendResponse(String string)
    {
        responseQueue.offer(string);
    }

    public void appendResponse(Object value)
    {
        responseQueue.offer(value == null ? "null" : value.toString());
    }
}
