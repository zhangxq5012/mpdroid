package com.bender.mpdroid;

import android.os.AsyncTask;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;

/**
 */
public class SongProgressFrame implements MpDroidActivityWidget
{
    private SeekBar songProgressSeekBar;
    private TextView songProgressTextView;
    private MpDroidActivity activity;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

    public void onCreate(MpDroidActivity activity)
    {
        this.activity = activity;
        songProgressSeekBar = (SeekBar) activity.findViewById(R.id.song_progress);
        songProgressSeekBar.setVisibility(View.INVISIBLE);
        songProgressTextView = (TextView) activity.findViewById(R.id.song_progress_text);

        songProgressSeekBar.setOnSeekBarChangeListener(new SongProgressSeekChangeListener());
    }

    public void onConnect()
    {
        mpdPlayerAdapterIF = activity.getMpdServiceAdapterIF().getPlayer();
        mpdPlayerAdapterIF.addSongProgressListener(new UIMpSongProgressListener());
    }

    public void onConnectionChange(boolean connected)
    {
        int visibility = connected ? View.VISIBLE : View.INVISIBLE;
        if (!connected)
        {
            songProgressSeekBar.setVisibility(visibility);
        }
        if (connected)
        {
            GetSongProgressTask getSongProgressTask = new GetSongProgressTask();
            getSongProgressTask.execute();
        }
        songProgressTextView.setVisibility(visibility);
    }

    private void updateSongProgressOnUI(MpdPlayerAdapterIF.MpdSongProgress mpdSongProgress)
    {
        final Integer currentTime = mpdSongProgress.getCurrentTime();
        final Integer totalTime = mpdSongProgress.getTotalTime();

        if (currentTime != null && totalTime != null)
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    if (totalTime > 0)
                    {
                        songProgressSeekBar.setMax(totalTime);
                        songProgressSeekBar.setProgress(currentTime);
                        songProgressSeekBar.setVisibility(View.VISIBLE);

                        String songProgressText = getSongProgressString(currentTime,totalTime);

                        songProgressTextView.setText(songProgressText);
                        songProgressTextView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        songProgressTextView.setText(getProgressString(currentTime, 2));
                        songProgressTextView.setVisibility(View.VISIBLE);
                        songProgressSeekBar.setVisibility(View.INVISIBLE);
                    }
                }

            };
            activity.runOnUiThread(runnable);
        }
    }

    private String getSongProgressString(Integer currentTime, Integer totalTime)
    {
        int length = totalTime;

        //calculate total time string
        String stringBuffer = "";
        int count = 0;
        do
        {
            int remainder = length % 60;
            length = length / 60;
            count++;
            stringBuffer = (String.format("%02d",remainder)) + stringBuffer;
            if (length > 0)
            {
                stringBuffer = ":" + stringBuffer;
            }

        } while (length > 0);

        //calculate current time string
        String currentTimeBuffer = getProgressString(currentTime, count);
        return currentTimeBuffer + " - " + stringBuffer;
    }

    private String getProgressString(Integer currentTime, int count)
    {
        int length;
        length = currentTime;
        String currentTimeBuffer = "";
        for (int i = 0 ; i < count; i++)
        {
            int remainder = length % 60;
            length = length / 60;
            currentTimeBuffer = String.format("%02d",remainder) + currentTimeBuffer;
            if (i < (count-1)) currentTimeBuffer = ":" + currentTimeBuffer;

        }
        while (length > 0)
        {
            int remainder = length % 60;
            length = length / 60;
            currentTimeBuffer = ":" + currentTimeBuffer;
            currentTimeBuffer = String.format("%02d",remainder) + currentTimeBuffer;
        }
        return currentTimeBuffer;
    }

    private void updateSongProgressTextOnUI(MpdPlayerAdapterIF.MpdSongProgress mpdSongProgress)
    {
        final Integer currentTime = mpdSongProgress.getCurrentTime();
        final Integer totalTime = mpdSongProgress.getTotalTime();
        final String songProgressText = totalTime > 0 ? getSongProgressString(currentTime,totalTime) : getProgressString(currentTime,2);

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                songProgressTextView.setText(songProgressText);
                songProgressTextView.setVisibility(View.VISIBLE);
            }
        };
        activity.runOnUiThread(runnable);

    }

    private class GetSongProgressTask extends AsyncTask<Void, Void, MpdPlayerAdapterIF.MpdSongProgress>
    {
        @Override
        protected MpdPlayerAdapterIF.MpdSongProgress doInBackground(Void... voids)
        {
            return mpdPlayerAdapterIF.getSongProgress();
        }

        @Override
        protected void onPostExecute(MpdPlayerAdapterIF.MpdSongProgress mpdSongProgress)
        {
            updateSongProgressOnUI(mpdSongProgress);
        }
    }

    private class SongProgressSeekChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            if (fromUser)
            {
                mpdPlayerAdapterIF.seek(progress);
                updateSongProgressTextOnUI(mpdPlayerAdapterIF.getSongProgress());
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    }

    private class UIMpSongProgressListener implements MpdPlayerAdapterIF.MpSongProgressListener
    {
        public void songProgressUpdate(MpdPlayerAdapterIF.MpdSongProgress songProgress)
        {
            updateSongProgressOnUI(songProgress);
        }
    }
}
