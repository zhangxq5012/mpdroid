package com.bender.mpdroid.mpdService;

/**
 * todo: replace with documentation
 */
public class MpdServerDriver
{
    public static void main(String[] args)
    {
        MpdServer mpdServer = new MpdServer();
        mpdServer.connect("localhost");

        System.out.println("version: " + mpdServer.getVersion());
    }
}
