package com.bender.mpdroid;

import android.os.AsyncTask;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;

/**
 */
class VolumeTask extends AsyncTask<Integer, Void, Void> {

    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

    public VolumeTask(MpdPlayerAdapterIF mpdPlayerAdapterIF) {
        this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Integer volume = params[0];
        mpdPlayerAdapterIF.setVolume(volume);
        return null;
    }
}
