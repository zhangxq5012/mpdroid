package com.bender.mpdlib;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
public class MockCommandStreamProvider implements SocketStreamProviderIF
{
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private List<String> commandQueue;
    private Queue<String> responseQueue;

    private boolean connected;

    public MockCommandStreamProvider() throws IOException
    {
        bufferedReader = mock(BufferedReader.class);
        bufferedWriter = mock(BufferedWriter.class);
        commandQueue = new LinkedList<String>();
        responseQueue = new LinkedList<String>();
        when(bufferedReader.readLine()).thenAnswer(new Answer<String>()
        {
            public String answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                return responseQueue.remove();
            }
        });
        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                commandQueue.add((String) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(bufferedWriter).write(anyString());
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

    public void appendServerResult(String s)
    {
        responseQueue.offer(s);
    }

    public void appendServerResult(Object value)
    {
        appendServerResult(value == null ? "null" : value.toString());
    }

    public void removeLastCommand()
    {
        List<String> list = (List<String>) responseQueue;
        list.remove(list.size() - 1);
    }

    public void assertLastCommandEquals(String command)
    {
        assertEquals(command, commandQueue.get(commandQueue.size() - 1));
    }
}
