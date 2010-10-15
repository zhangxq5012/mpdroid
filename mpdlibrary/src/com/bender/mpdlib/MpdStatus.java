package com.bender.mpdlib;

import com.bender.mpdlib.commands.StatusTuple;

import java.util.StringTokenizer;

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
        StringTokenizer stringTokenizer = new StringTokenizer(line, ":");
        String statusString = stringTokenizer.nextToken();
        for (MpdStatus mpdStatus : values())
        {
            if (mpdStatus.toString().equals(statusString))
            {
                status = mpdStatus;
            }
        }
        String valueString = stringTokenizer.nextToken().trim();
        StatusTuple statusTuple = new StatusTuple(status, valueString);
        return statusTuple;
    }
}
