package com.bender.mpdroid.mpdService;

import org.bff.javampd.objects.MPDAlbum;
import org.bff.javampd.objects.MPDArtist;
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
        String name = javaMpdSong.getName();
        if (name == null)
        {
            name = javaMpdSong.getFile();
            name = name.substring(name.lastIndexOf('/') + 1, name.length());
            int endIndex = name.lastIndexOf('.');
            if (endIndex != -1)
            {
                name = name.substring(0, endIndex);
            }
        }
        return name;
    }

    public String getArtist()
    {
        String ret = null;
        MPDArtist artist = javaMpdSong.getArtist();
        if (artist != null)
        {
            ret = artist.getName();
        }
        return ret;
    }

    public String getAlbumName()
    {
        String albumName = null;
        MPDAlbum album = javaMpdSong.getAlbum();
        if (album != null)
        {
            albumName = album.getName();
        }
        return albumName;
    }
}
