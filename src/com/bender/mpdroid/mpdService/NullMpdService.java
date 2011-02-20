package com.bender.mpdroid.mpdService;

/**
 */
public class NullMpdService implements MpdServiceAdapterIF
{
    public void connect(String server, int port, String password)
    {
    }

    public void connect(String server, int port)
    {
    }

    public void connect(String server)
    {
    }

    public void disconnect()
    {
    }

    public boolean isConnected()
    {
        return false;
    }

    public String getServerVersion()
    {
        return null;
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        return new NullPlayerAdapter();
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return new NullPlaylistAdapter();
    }

    public MpdOptionsIF getOptions()
    {
        return new NullMpdOptions();
    }

    public void addConnectionListener(MpdConnectionListenerIF connectionListenerIF)
    {
    }
}
