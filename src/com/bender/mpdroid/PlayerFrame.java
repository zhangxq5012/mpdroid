package com.bender.mpdroid;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;

/**
 */
public class PlayerFrame implements MpDroidActivityWidget
{
    private Button playButton;
    private Button stopButton;
    private Button nextButton;
    private Button prevButton;
    private MpDroidActivity activity;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

    public void onCreate(MpDroidActivity activity)
    {
        this.activity = activity;
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        playButton = (Button) activity.findViewById(R.id.play);
        stopButton = (Button) activity.findViewById(R.id.stop);
        nextButton = (Button) activity.findViewById(R.id.next);
        prevButton = (Button) activity.findViewById(R.id.prev);
        playButton.setOnClickListener(buttonClickListener);
        stopButton.setOnClickListener(buttonClickListener);
        nextButton.setOnClickListener(buttonClickListener);
        prevButton.setOnClickListener(buttonClickListener);
    }

    public void onConnect()
    {
        mpdPlayerAdapterIF = activity.getMpdServiceAdapterIF().getPlayer();
        mpdPlayerAdapterIF.addPlayStatusListener(new PlayListener());
        updatePlayStatusOnUI(mpdPlayerAdapterIF.getPlayStatus());
    }

    public void onConnectionChange(boolean connected)
    {
        playButton.setEnabled(connected);
        stopButton.setEnabled(connected);
        nextButton.setEnabled(connected);
        prevButton.setEnabled(connected);

    }

    private void updatePlayStatusOnUI(final MpdPlayerAdapterIF.PlayStatus playStatus)
    {
        if (playStatus == null)
        {
            throw new NullPointerException();
        }
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                switch (playStatus)
                {
                    case Playing:
                        playButton.setText(activity.getString(R.string.pause));
                        break;
                    case Paused:
                        playButton.setText(activity.getString(R.string.play));
                    default:
                        playButton.setText(activity.getString(R.string.play));
                }
            }
        };
        activity.runOnUiThread(runnable);
    }

    private class ButtonClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            if (view == playButton)
            {
                PlayTask playTask = new PlayTask(mpdPlayerAdapterIF);
                playTask.execute();
            }
            else if (view == stopButton)
            {
                StopTask stopTask = new StopTask(mpdPlayerAdapterIF);
                stopTask.execute();
            }
            else if (view == nextButton)
            {
                NextTask nextTask = new NextTask(mpdPlayerAdapterIF);
                nextTask.execute();
            }
            else if (view == prevButton)
            {
                PrevTask prevTask = new PrevTask(mpdPlayerAdapterIF);
                prevTask.execute();
            }
        }
    }

    private class PlayListener implements MpdPlayerAdapterIF.MpdPlayStatusListener
    {
        public void playStatusUpdated(final MpdPlayerAdapterIF.PlayStatus playStatus)
        {
            updatePlayStatusOnUI(playStatus);
        }
    }

    private static class PlayTask extends AsyncTask<Object, Object, Object>
    {
        private MpdPlayerAdapterIF mpdPlayerAdapterIF;

        public PlayTask(MpdPlayerAdapterIF mpdPlayerAdapterIF)
        {
            this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
        }

        @Override
        protected Object doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.playOrPause();
            return null;
        }
    }

    private static class NextTask extends AsyncTask<Object, Object, Object>
    {
        private MpdPlayerAdapterIF mpdPlayerAdapterIF;

        NextTask(MpdPlayerAdapterIF mpdPlayerAdapterIF)
        {
            this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
        }

        @Override
        protected Object doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.next();
            return null;
        }
    }

    private static class PrevTask extends AsyncTask<Object, Object, Object>
    {

        private MpdPlayerAdapterIF mpdPlayerAdapterIF;

        public PrevTask(MpdPlayerAdapterIF mpdPlayerAdapterIF)
        {
            this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
        }

        @Override
        protected Object doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.prev();
            return null;
        }
    }

    private static class StopTask extends AsyncTask<Object, Object, Object>
    {

        private MpdPlayerAdapterIF mpdPlayerAdapterIF;

        public StopTask(MpdPlayerAdapterIF mpdPlayerAdapterIF)
        {
            this.mpdPlayerAdapterIF = mpdPlayerAdapterIF;
        }

        @Override
        protected MpdPlayerAdapterIF.PlayStatus doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.stop();
            return null;
        }
    }
}
