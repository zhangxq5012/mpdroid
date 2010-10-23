package com.bender.mpdroid.mpdService;

/**
 * todo: replace with documentation
 */
public class SongNameDecorator implements MpdSongAdapterIF
{
    private MpdSongAdapterIF implementation;

    public SongNameDecorator(MpdSongAdapterIF implementation)
    {
        this.implementation = implementation;
    }

    public String getSongName()
    {
        String name = implementation.getSongName();
        if (name == null)
        {
            name = implementation.getFile();
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
        return implementation.getArtist();
    }

    public String getAlbumName()
    {
        return implementation.getAlbumName();
    }

    public String getFile()
    {
        return implementation.getFile();
    }

    public String getDate()
    {
        return implementation.getDate();
    }
}
