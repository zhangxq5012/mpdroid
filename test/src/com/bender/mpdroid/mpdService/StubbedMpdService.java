package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.ConnectionListener;
import com.bender.mpdlib.MulticastConnectionListener;

/**
 */
public class StubbedMpdService implements MpdServiceAdapterIF
{
    private boolean connected;

    private MulticastConnectionListener listeners = new MulticastConnectionListener();

    public void connect(String server, int port, String password)
    {
        connected = true;
        updateConnectionListeners();
    }

    public void connect(String server, int port)
    {
        connect(server, port, null);
    }

    public void connect(String server)
    {
        connect(server, -1);
    }

    public void disconnect()
    {
        connected = false;
        updateConnectionListeners();
    }

    private void updateConnectionListeners()
    {
        listeners.connectionChanged(connected);
    }

    public boolean isConnected()
    {
        return connected;
    }

    public String getServerVersion()
    {
        return "stub";
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

    public void addConnectionListener(final MpdConnectionListenerIF connectionListenerIF)
    {
        listeners.addConnectionListener(new ConnectionListener()
        {
            public void connectionChanged(boolean isConnected)
            {
                connectionListenerIF.connectionStateUpdated(isConnected);
            }
        });
    }
}
