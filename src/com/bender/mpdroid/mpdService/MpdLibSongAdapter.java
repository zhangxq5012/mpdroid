package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.SongInfo;

/**
 */
class MpdLibSongAdapter implements MpdSongAdapterIF {

    private SongInfo songInfo;

    public MpdLibSongAdapter(SongInfo songInfo) {
        this.songInfo = songInfo;
    }

    public String getSongName() {
        return songInfo.getValue(SongInfo.SongAttributeType.Title);
    }

    public String getArtist() {
        return songInfo.getValue(SongInfo.SongAttributeType.Artist);
    }

    public String getAlbumName() {
        return songInfo.getValue(SongInfo.SongAttributeType.Album);
    }

    public String getFile() {
        return songInfo.getValue(SongInfo.SongAttributeType.file);
    }

    public String getDate() {
        return songInfo.getValue(SongInfo.SongAttributeType.Date);
    }

    public Integer getSongLength() {
        try {
            return Integer.valueOf(songInfo.getValue(SongInfo.SongAttributeType.Time));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public int getId() {
        return Integer.parseInt(songInfo.getValue(SongInfo.SongAttributeType.Id));
    }
}
