package com.bender.mpdroid.mpdService;

import org.bff.javampd.objects.MPDSong;

/**
 * JavaMPD implementation of #MpdSongAdapterIF
 */
class JavaMPDSongAdapter implements MpdSongAdapterIF
{
    private MPDSong javaMpdSong;

    public JavaMPDSongAdapter(MPDSong currentSong)
    {
        javaMpdSong = currentSong;
    }

    public String getSongName()
    {
        return javaMpdSong.getName();
    }
}
