package com.bender.mpdlib;

import com.bender.mpdlib.commands.StatusTuple;

/**
 * todo: replace with documentation
 */
public enum MpdStatus
{
    volume,
    repeat,
    random,
    single,
    consume,
    playlist,
    playlistlength,
    xfade,
    state,
    song,
    songid,
    time,
    bitrate,
    audio,
    nextsong,
    nextsongid;

    public static StatusTuple parse(String line)
    {
        MpdStatus status = null;
        String[] strings = line.split(":");
        String statusString = strings[0].trim();
        for (MpdStatus mpdStatus : values())
        {
            if (mpdStatus.toString().equals(statusString))
            {
                status = mpdStatus;
            }
        }
        String valueString = strings[1].trim();
        StatusTuple statusTuple = new StatusTuple(status, valueString);
        return statusTuple;
    }
}
