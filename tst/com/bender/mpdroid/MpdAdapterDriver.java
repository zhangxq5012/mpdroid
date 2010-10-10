package com.bender.mpdroid;

/**
 * This is a driver for making a connection to a mpd server on the localhost
 */
public class MpdAdapterDriver
{

    public static void main(String[] args)
    {
        final String server = "localhost";
        System.out.println("Using authentication:");
        MpdAdapterIF adapterIF = MpdAdapterFactory.createAdapter();
        adapterIF.connect(server, 6600, "dancured62");
        System.out.println("Connected: " + adapterIF.isConnected());
        adapterIF.disconnect();

        System.out.println("No authentication:");
        adapterIF = MpdAdapterFactory.createAdapter();
        adapterIF.connect(server);
        System.out.println("Connected: " + adapterIF.isConnected());
        System.out.println("Version: " + adapterIF.getServerVersion());
        adapterIF.disconnect();
    }
}
