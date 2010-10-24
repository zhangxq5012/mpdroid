package com.bender.mpdroid;

import android.os.AsyncTask;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;

/**
 */
class VolumeTask extends AsyncTask<Integer, Object, Object>
{

    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

    public VolumeTask(MpdPlayerAdapterIF mpdPlayerAdapterIF)
    {
        this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
    }

    @Override
    protected Integer doInBackground(Integer... params)
    {
        Integer volume = params[0];
        mpdPlayerAdapterIF.setVolume(volume);
        return null;
    }
}
