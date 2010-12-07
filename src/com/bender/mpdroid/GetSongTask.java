package com.bender.mpdroid;

import android.os.AsyncTask;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;
import com.bender.mpdroid.mpdService.MpdSongAdapterIF;
import com.bender.mpdroid.mpdService.SongNameDecorator;

/**
 */
class GetSongTask extends AsyncTask<Void, Void, MpdSongAdapterIF> {
    private MpDroidActivity mpDroidActivity;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

    public GetSongTask(MpDroidActivity mpDroidActivity, MpdPlayerAdapterIF mpdPlayerAdapterIF) {
        this.mpDroidActivity = mpDroidActivity;
        this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
    }

    @Override
    protected MpdSongAdapterIF doInBackground(Void... objects) {
        MpdSongAdapterIF currentSong = mpdPlayerAdapterIF.getCurrentSong();
        return currentSong;
    }

    @Override
    protected void onPostExecute(MpdSongAdapterIF mpdSongAdapterIF) {
        mpDroidActivity.updateSongOnUI(new SongNameDecorator(mpdSongAdapterIF));
    }

}
