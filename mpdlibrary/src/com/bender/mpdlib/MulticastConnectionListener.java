package com.bender.mpdlib;

/**
 */
public class MulticastConnectionListener implements ConnectionListener
{
    private ConnectionListener next = new NullConnectionListener();
    private ConnectionListener implementation;

    public ConnectionListener addConnectionListener(ConnectionListener connectionListener)
    {
        if (implementation == null)
        {
            implementation = connectionListener;
        } else
        {
            next = new MulticastConnectionListener().addConnectionListener(connectionListener);
        }
        return this;
    }

    public void connectionChanged(boolean isConnected)
    {
        if (implementation != null)
        {
            implementation.connectionChanged(isConnected);
            next.connectionChanged(isConnected);
        }
    }
}
