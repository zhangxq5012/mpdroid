package com.bender.mpdroid;

/**
 * This is a stubbed mpd adapter. It is meant to be used for testing.
 */
public class StubbedMpdAdapater implements MpdAdapterIF
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

    public void playOrPause()
    {
        //todo: implement
    }
}
