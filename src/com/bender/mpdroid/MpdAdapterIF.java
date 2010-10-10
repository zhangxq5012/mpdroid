package com.bender.mpdroid;

/**
 * todo: replace with documentation
 */
public interface MpdAdapterIF
{
    void connect(String server, int port, String password);

    void connect(String server);

    void disconnect();

    boolean isConnected();

    String getServerVersion();

    void playOrPause();
}
