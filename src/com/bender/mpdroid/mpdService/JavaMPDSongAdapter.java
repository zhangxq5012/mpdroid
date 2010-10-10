package com.bender.mpdroid.mpdService;

import org.bff.javampd.objects.MPDSong;

/**
 * todo: replace with documentation
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
