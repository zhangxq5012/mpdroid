package com.bender.mpdroid;

import com.bender.mpdroid.mpdService.MpdAdapterFactory;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;

/**
 * This is a driver for making a connection to a mpd server on the localhost
 */
public class MpdAdapterDriver
{

    public static void main(String[] args)
    {
        final String server = "192.168.2.2";
        System.out.println("Using authentication:");
        MpdServiceAdapterIF serviceAdapterIF = MpdAdapterFactory.createAdapter();
        serviceAdapterIF.connect(server, 6600, "dancured62");
        System.out.println("Connected: " + serviceAdapterIF.isConnected());
        serviceAdapterIF.disconnect();

        System.out.println();
        System.out.println("No authentication:");
        serviceAdapterIF = MpdAdapterFactory.createAdapter();
        serviceAdapterIF.connect(server);
        System.out.println("Connected: " + serviceAdapterIF.isConnected());
        System.out.println("Version: " + serviceAdapterIF.getServerVersion());
        serviceAdapterIF.disconnect();
    }
}
