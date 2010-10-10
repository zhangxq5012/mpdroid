package com.bender.mpdroid;

/**
 * This is a stubbed mpd adapter. It is meant to be used for testing.
 */
public class StubbedMpdAdapater implements MpdAdapterIF
{
    private boolean connected;
    public int connectCount;
    public int disconnectCount;
    private Integer volume;

    public PlayStatus getPlayStatus()
    {
        return PlayStatus.Stopped;
    }

    public void next()
    {
    }

    public void prev()
    {
    }

    public Integer setVolume(Integer volume)
    {
        this.volume = volume;
        return getVolume();
    }

    public Integer getVolume()
    {
        return this.volume;
    }

    public Boolean toggleMute()
    {
        return false;
    }

    public void connect(String server, int port, String password)
    {
        connect();
    }

    public void connect(String server, int port)
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

    public PlayStatus playOrPause()
    {
        return PlayStatus.Stopped;
    }
}
