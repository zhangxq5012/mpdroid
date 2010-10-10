package com.bender.mpdroid;

/**
 * todo: replace with documentation
 */
public class StubbedMpdActivity implements MpdAdapterIF
{
    private boolean connected;
    public int connectCount;
    public int disconnectCount;

    public void connect(String server, int port, String password)
    {
        connect();
    }

    public void connect(String server)
    {
        connect();
    }

    private void connect()
    {
        connected = true;
        connectCount++;
    }

    public void disconnect()
    {
        connected = false;
        disconnectCount++;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public String getServerVersion()
    {
        return "Stubbed MPD Server";
    }
}
