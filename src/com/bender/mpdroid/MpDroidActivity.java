package com.bender.mpdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bender.mpdroid.mpdService.*;

/**
 * Main activity for the mpd droid application
 */
public class MpDroidActivity extends Activity
{
    private static final String TAG = MpDroidActivity.class.getSimpleName();
    private static final int REQUEST_PREFERENCES = 1;

    private TextView serverTextView;
    private TextView portTextView;
    private CheckBox useAuthenticationCheckbox;
    private Button connectButton;
    private SeekBar volumeSeekBar;
    private Button muteButton;
    private TextView songNameTextView;
    private TextView songDetailsTextView;
    private SeekBar songProgressSeekBar;
    private TextView songProgressTextView;

    private MpdPreferences myPreferences;

    private MpdServiceAdapterIF mpdServiceAdapterIF;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;
    private MpDroidActivityWidget mpDroidActivityWidget;

    public MpDroidActivity()
    {
        mpDroidActivityWidget = new PlayerFrame();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        mpdServiceAdapterIF = MpdAdapterFactory.createAdapter();
        myPreferences = new MpdPreferences(this);
        mpDroidActivityWidget.onCreate(this);
        initializeWidgets();
        initializeListeners();
        updatePreferencesDisplay();
        updateConnectedStatusOnUI(false);
        Log.v(TAG, "onCreate: DONE");
    }

    private void initializeWidgets()
    {
        serverTextView = (TextView) findViewById(R.id.server_name);
        portTextView = (TextView) findViewById(R.id.port);
        useAuthenticationCheckbox = (CheckBox) findViewById(R.id.use_authentication);
        connectButton = (Button) findViewById(R.id.connect);
        volumeSeekBar = (SeekBar) findViewById(R.id.volume);
        muteButton = (Button) findViewById(R.id.mute);
        songNameTextView = (TextView) findViewById(R.id.song_name);
        songDetailsTextView = (TextView) findViewById(R.id.song_details);
        songProgressSeekBar = (SeekBar) findViewById(R.id.song_progress);
        songProgressSeekBar.setVisibility(View.INVISIBLE);
        songProgressTextView = (TextView) findViewById(R.id.song_progress_text);
    }

    private void initializeListeners()
    {
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        connectButton.setOnClickListener(buttonClickListener);
        volumeSeekBar.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener());
        muteButton.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_PREFERENCES)
        {
            Log.v(TAG, "Preferences Updated");
            updatePreferencesDisplay();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean ret = true;
        switch (item.getItemId())
        {
            case R.id.settings_menu:
                openPreferences();
                break;
            default:
                ret = super.onOptionsItemSelected(item);
        }
        return ret;
    }

    private void openPreferences()
    {
        Intent launchPreferencesIntent = new Intent();
        launchPreferencesIntent.setClass(MpDroidActivity.this, MpDroidPreferenceActivity.class);
        startActivityForResult(launchPreferencesIntent, REQUEST_PREFERENCES);
    }

    private void updatePreferencesDisplay()
    {
        String server = myPreferences.getServer();
        serverTextView.setText(getText(R.string.server) + ": " + server);
        String port = String.valueOf(myPreferences.getPort());
        portTextView.setText(getText(R.string.port) + ": " + port);
        boolean useAuthentication = myPreferences.useAuthentication();
        useAuthenticationCheckbox.setChecked(useAuthentication);
    }

    MpdServiceAdapterIF getMpdServiceAdapterIF()
    {
        return mpdServiceAdapterIF;
    }

    private void updateConnectedStatusOnUI(Boolean connected)
    {
        String text = connected ? getString(R.string.disconnect) : getString(R.string.connect);
        connectButton.setText(text);
        volumeSeekBar.setEnabled(connected);
        muteButton.setEnabled(connected);
        int visibility = connected ? View.VISIBLE : View.INVISIBLE;
        songNameTextView.setVisibility(visibility);
        songDetailsTextView.setVisibility(visibility);
        if (!connected)
        {
            songProgressSeekBar.setVisibility(visibility);
        }
        songProgressTextView.setVisibility(visibility);
        if (connected)
        {
            GetVolumeTask getVolumeTask = new GetVolumeTask();
            getVolumeTask.execute();
            GetSongTask getSongTask = new GetSongTask(this, mpdPlayerAdapterIF);
            getSongTask.execute();
            GetSongProgressTask getSongProgressTask = new GetSongProgressTask();
            getSongProgressTask.execute();
        }
        mpDroidActivityWidget.onConnectionChange(connected);
    }


    private class ConnectTask extends AsyncTask<Object, MpdPlayerAdapterIF.PlayStatus, Boolean>
    {
        @Override
        protected Boolean doInBackground(Object... unused)
        {
            String server = myPreferences.getServer();
            int port = myPreferences.getPort();
            boolean useAuthentication = myPreferences.useAuthentication();
            boolean usePort = myPreferences.usePort();
            if (useAuthentication)
            {
                String password = myPreferences.getPassword();
                mpdServiceAdapterIF.connect(server, port, password);
            }
            else if (usePort)
            {
                mpdServiceAdapterIF.connect(server, port);
            }
            else
            {
                mpdServiceAdapterIF.connect(server);
            }
            boolean connected = mpdServiceAdapterIF.isConnected();
            mpdServiceAdapterIF.addConnectionListener(new MyMpdConnectionListenerIF());

            if (connected)
            {
                mpdPlayerAdapterIF = mpdServiceAdapterIF.getPlayer();
                mpdPlayerAdapterIF.addSongChangeListener(new SongListener());
                mpdPlayerAdapterIF.addVolumeListener(new UiVolumeListener());
                mpdPlayerAdapterIF.addSongProgressListener(new UIMpSongProgressListener());
                mpDroidActivityWidget.onConnect();

                Log.v(TAG, "MPD Server version: " + mpdServiceAdapterIF.getServerVersion());
            }
            String connectedText = makeConnectedText(server, connected);
            Log.v(TAG, connectedText);
            return connected;
        }

        private String makeConnectedText(String server, boolean connected)
        {
            return connected ? "Connected to " + server + "!"
                    : "Unable to Connect to " + server + "!";
        }

        @Override
        protected void onPostExecute(Boolean connected)
        {
            updateConnectedStatusOnUI(connected);
            if (!connected)
            {
                Toast.makeText(MpDroidActivity.this,
                        makeConnectedText(myPreferences.getServer(), connected), Toast.LENGTH_SHORT).show();
            }
        }

        private class MyMpdConnectionListenerIF implements MpdConnectionListenerIF
        {
            public void connectionStateUpdated(final boolean connected)
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        updateConnectedStatusOnUI(connected);
                    }
                });
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


    private class ButtonClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            if (view == connectButton)
            {
                boolean connected = mpdServiceAdapterIF.isConnected();
                if (connected)
                {
                    DisconnectTask disconnectTask = new DisconnectTask();
                    disconnectTask.execute();
                }
                else
                {
                    ConnectTask connectTask = new ConnectTask();
                    connectTask.execute();
                }
            }
            else if (view == muteButton)
            {
                ToggleMuteTask toggleMuteTask = new ToggleMuteTask();
                toggleMuteTask.execute();
            }
        }
    }

    private class DisconnectTask extends AsyncTask<Object, Object, Boolean>
    {
        @Override
        protected Boolean doInBackground(Object... ignored)
        {
            mpdServiceAdapterIF.disconnect();
            return mpdServiceAdapterIF.isConnected();
        }

        @Override
        protected void onPostExecute(Boolean connected)
        {
            updateConnectedStatusOnUI(connected);
        }
    }

    private class VolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser)
        {
            if (seekBar == volumeSeekBar && fromUser)
            {
                VolumeTask volumeTask = new VolumeTask(mpdPlayerAdapterIF);
                volumeTask.execute(volume);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }

    }

    private class ToggleMuteTask extends AsyncTask<Object, Integer, Object>
    {
        @Override
        protected Boolean doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.toggleMute();
            return null;
        }
    }

    private class GetVolumeTask extends AsyncTask<Object, Object, Integer>
    {
        @Override
        protected Integer doInBackground(Object... objects)
        {
            return mpdPlayerAdapterIF.getVolume();
        }

        @Override
        protected void onPostExecute(Integer volume)
        {
            volumeSeekBar.setProgress(volume);
        }
    }

    void updateSongOnUI(MpdSongAdapterIF mpdSongAdapterIF)
    {
        String songName = mpdSongAdapterIF.getSongName();
        songNameTextView.setText(songName);
        String artist = mpdSongAdapterIF.getArtist();
        String album = mpdSongAdapterIF.getAlbumName();
        String date = mpdSongAdapterIF.getDate();
        StringBuilder details = new StringBuilder();
        if (artist != null)
        {
            details.append("by ").append(artist);
        }
        if (album != null)
        {
            details.append(" from " + album);
        }
        if (date != null)
        {
            details.append(" (" + date + ")");
        }
        songDetailsTextView.setText(details);
//        Integer length = mpdSongAdapterIF.getSongLength();
//        if (length != null)
//        {
//            CharSequence lengthString = getLengthString(length);
//            songProgressSeekBar.setMax(length);
//            songProgressSeekBar.setProgress(0);
//            songProgressSeekBar.setVisibility(View.VISIBLE);
//            songProgressTextView.setText(lengthString);
//            songProgressTextView.setVisibility(View.VISIBLE);
//        }
//        else
//        {
//            songProgressTextView.setVisibility(View.INVISIBLE);
//        }
    }

    private static CharSequence getLengthString(Integer length)
    {
        String stringBuffer = "";
        do
        {
            int remainder = length % 60;
            stringBuffer = String.format("%02d", remainder) + stringBuffer;
            if ((length / 60) > 0)
            {
                stringBuffer = ":" + stringBuffer;
            }
        }
        while ((length = length / 60) > 0);
        return stringBuffer;
    }

    private class SongListener implements MpdSongListener
    {
        public void songUpdated(final MpdSongAdapterIF song)
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    updateSongOnUI(new SongNameDecorator(song));
                }
            };
            runOnUiThread(runnable);
        }
    }


    private class UiVolumeListener implements MpdPlayerAdapterIF.MpdVolumeListener
    {
        public void volumeUpdated(final Integer volume)
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    volumeSeekBar.setProgress(volume);
                }
            };
            runOnUiThread(runnable);
        }
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

                        String songProgressText = getLengthString(currentTime) + "-" + getLengthString(totalTime);

                        songProgressTextView.setText(songProgressText);
                        songProgressTextView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        songProgressTextView.setText(getLengthString(currentTime));
                        songProgressTextView.setVisibility(View.VISIBLE);
                        songProgressSeekBar.setVisibility(View.INVISIBLE);
                    }
                }
            };
            runOnUiThread(runnable);
        }
    }
}
