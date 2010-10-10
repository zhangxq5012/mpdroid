package com.bender.mpdroid;

/**
 * This is a driver for making a connection to a mpd server on the localhost
 */
public class MpdAdapterDriver
{

    public static void main(String[] args)
    {
        MpdAdapter adapter = new MpdAdapter();
        adapter.connect("localhost", 6600, "dancured62");
        System.out.println("Connected: " + adapter.isConnected());
        adapter.disconnect();

        adapter = new MpdAdapter();
        adapter.connect("localhost");
        System.out.println("Connected: " + adapter.isConnected());
        adapter.disconnect();
    }
}
