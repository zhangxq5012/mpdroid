package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public class SeekArg
{
    public final Integer songId;
    public final Integer position;

    public SeekArg(Integer songId, Integer position)
    {
        this.position = position;
        this.songId = songId;
    }

    @Override
    public String toString()
    {
        return songId + " " + position;
    }
}
