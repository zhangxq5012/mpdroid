package com.bender.mpdroid;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;

/**
 */
public class SongProgressFrame implements MpDroidActivityWidget
{
    private SeekBar songProgressSeekBar;
    private TextView songProgressTextView;
    private TextView totalTimeTextView;
    private MpDroidActivity activity;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;
    private MpdServiceAdapterIF mpdServiceAdapterIF;

    private Handler myHandler = new Handler();
    private MyRunnable runnable = new MyRunnable();

    public void onCreate(MpDroidActivity activity)
    {
        this.activity = activity;
        songProgressSeekBar = (SeekBar) activity.findViewById(R.id.song_progress);
        songProgressSeekBar.setVisibility(View.INVISIBLE);
        songProgressTextView = (TextView) activity.findViewById(R.id.song_progress_text);
        totalTimeTextView = (TextView) activity.findViewById(R.id.total_time);

        songProgressSeekBar.setOnSeekBarChangeListener(new SongProgressSeekChangeListener());
    }

    public void onConnect()
    {
        mpdServiceAdapterIF = activity.getMpdServiceAdapterIF();
        mpdPlayerAdapterIF = mpdServiceAdapterIF.getPlayer();
        mpdPlayerAdapterIF.addSongProgressListener(new UIMpSongProgressListener());
        mpdPlayerAdapterIF.addPlayStatusListener(new MpdPlayerAdapterIF.MpdPlayStatusListener()
        {
            public void playStatusUpdated(MpdPlayerAdapterIF.PlayStatus playStatus)
            {
                if (playStatus.equals(MpdPlayerAdapterIF.PlayStatus.Paused))
                {
                    myHandler.postDelayed(runnable, 500);
                }
            }
        });
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
            myHandler.postDelayed(runnable, 500);
        }
        songProgressTextView.setVisibility(visibility);
        totalTimeTextView.setVisibility(visibility);
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

                        updateSongProgressText(currentTime, totalTime);

                        songProgressTextView.setVisibility(View.VISIBLE);
                        totalTimeTextView.setVisibility(View.VISIBLE);
                    } else
                    {
                        songProgressTextView.setText(getProgressString(currentTime, 2));
                        songProgressTextView.setVisibility(View.VISIBLE);
                        songProgressSeekBar.setVisibility(View.INVISIBLE);
                        totalTimeTextView.setVisibility(View.INVISIBLE);
                    }
                }

            };
            activity.runOnUiThread(runnable);
        }
    }

    private void updateSongProgressText(Integer currentTime, Integer totalTime)
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
            stringBuffer = (String.format("%02d", remainder)) + stringBuffer;
            if (length > 0)
            {
                stringBuffer = ":" + stringBuffer;
            }

        } while (length > 0);

        totalTimeTextView.setText(stringBuffer);

        //calculate current time string
        String currentTimeBuffer = getProgressString(currentTime, count);
        songProgressTextView.setText(currentTimeBuffer);
    }

    private String getProgressString(Integer currentTime, int count)
    {
        int length;
        length = currentTime;
        String currentTimeBuffer = "";
        for (int i = 0; i < count; i++)
        {
            int remainder = length % 60;
            length = length / 60;
            currentTimeBuffer = String.format("%02d", remainder) + currentTimeBuffer;
            if (i < (count - 1)) currentTimeBuffer = ":" + currentTimeBuffer;

        }
        while (length > 0)
        {
            int remainder = length % 60;
            length = length / 60;
            currentTimeBuffer = ":" + currentTimeBuffer;
            currentTimeBuffer = String.format("%02d", remainder) + currentTimeBuffer;
        }
        return currentTimeBuffer;
    }

    private void updateSongProgressTextOnUI(MpdPlayerAdapterIF.MpdSongProgress mpdSongProgress)
    {
        final Integer currentTime = mpdSongProgress.getCurrentTime();
        final Integer totalTime = mpdSongProgress.getTotalTime();

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                updateSongProgressText(currentTime, totalTime);
                songProgressTextView.setVisibility(View.VISIBLE);
                totalTimeTextView.setVisibility(View.VISIBLE);
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

    private class MyRunnable implements Runnable
    {
        public void run()
        {
            if (mpdServiceAdapterIF != null && mpdServiceAdapterIF.isConnected())
            {
                switch (mpdPlayerAdapterIF.getPlayStatus())
                {
                    case Paused:
                        int visibility = songProgressTextView.getVisibility();
                        songProgressTextView.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                        myHandler.postDelayed(this, 500);
                        break;
                    default:
                        songProgressTextView.setVisibility(View.VISIBLE);
                        myHandler.removeCallbacks(this);
                        break;
                }
            } else
            {
                songProgressTextView.setVisibility(View.INVISIBLE);
                myHandler.removeCallbacks(this);
            }
        }
    }
}
