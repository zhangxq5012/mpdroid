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
import com.bender.mpdroid.mpdService.MpdAdapterFactory;
import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;

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
    private Button playButton;
    private Button nextButton;
    private Button prevButton;
    private SeekBar volumeSeekBar;
    private Button muteButton;

    private MpdPreferences myPreferences;
    private MpdServiceAdapterIF mpdServiceAdapterIF;
    private MpdPlayerAdapterIF mpdPlayerAdapterIF;

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
        initializeWidgets();
        initializeListeners();
        updatePreferencesDisplay();
        Log.v(TAG, "onCreate: DONE");
    }

    private void initializeWidgets()
    {
        serverTextView = (TextView) findViewById(R.id.server_name);
        portTextView = (TextView) findViewById(R.id.port);
        useAuthenticationCheckbox = (CheckBox) findViewById(R.id.use_authentication);
        connectButton = (Button) findViewById(R.id.connect);
        playButton = (Button) findViewById(R.id.play);
        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.prev);
        volumeSeekBar = (SeekBar) findViewById(R.id.volume);
        muteButton = (Button) findViewById(R.id.mute);
    }

    private void initializeListeners()
    {
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        connectButton.setOnClickListener(buttonClickListener);
        playButton.setOnClickListener(buttonClickListener);
        nextButton.setOnClickListener(buttonClickListener);
        prevButton.setOnClickListener(buttonClickListener);
        volumeSeekBar.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener());
        muteButton.setOnClickListener(buttonClickListener);
    }

    private void connect()
    {
        ConnectTask connectTask = new ConnectTask();
        connectTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_PREFERENCES)
        {
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
        String port = String.valueOf(myPreferences.getDefaultPort());
        portTextView.setText(getText(R.string.port) + ": " + port);
        boolean useAuthentication = myPreferences.useAuthentication();
        useAuthenticationCheckbox.setChecked(useAuthentication);
    }

    /**
     * For unit test.
     *
     * @return mpd adapter
     */
    MpdServiceAdapterIF getMpdServiceAdapterIF()
    {
        return mpdServiceAdapterIF;
    }

    private void updateConnectedStatusOnUI(Boolean connected)
    {
        String text = connected ? getString(R.string.disconnect) : getString(R.string.connect);
        connectButton.setText(text);
        playButton.setEnabled(connected);
        nextButton.setEnabled(connected);
        prevButton.setEnabled(connected);
        volumeSeekBar.setEnabled(connected);
        muteButton.setEnabled(connected);
        if (connected)
        {
            volumeSeekBar.setProgress(mpdPlayerAdapterIF.getVolume());
        }
    }

    private void updatePlayStatusOnUI(MpdPlayerAdapterIF.PlayStatus playStatus)
    {
        switch (playStatus)
        {
            case Playing:
                playButton.setText(getString(R.string.pause));
                break;
            case Paused:
                playButton.setText(getString(R.string.play));
            default:
                playButton.setText(getString(R.string.play));
        }
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

            mpdPlayerAdapterIF = mpdServiceAdapterIF.getPlayer();
            MpdPlayerAdapterIF.PlayStatus playStatus = mpdPlayerAdapterIF.getPlayStatus();
            publishProgress(playStatus);

            String connectedText = makeConnectedText(server, connected);
            Log.v(TAG, connectedText);
            Log.v(TAG, "MPD Server version: " + mpdServiceAdapterIF.getServerVersion());
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
            Toast.makeText(MpDroidActivity.this, makeConnectedText(myPreferences.getServer(), connected), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(MpdPlayerAdapterIF.PlayStatus... values)
        {
            updatePlayStatusOnUI(values[0]);
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
                    disconnect();
                }
                else
                {
                    connect();
                }
            }
            else if (view == playButton)
            {
                play();
            }
            else if (view == nextButton)
            {
                NextTask nextTask = new NextTask();
                nextTask.execute();
            }
            else if (view == prevButton)
            {
                PrevTask prevTask = new PrevTask();
                prevTask.execute();
            }
            else if (view == muteButton)
            {
                ToggleMuteTask toggleMuteTask = new ToggleMuteTask();
                toggleMuteTask.execute();
            }
        }

        private class PrevTask extends AsyncTask<Object, Object, Object>
        {
            @Override
            protected Object doInBackground(Object... objects)
            {
                mpdPlayerAdapterIF.prev();
                return null;
            }
        }
    }

    private void play()
    {
        PlayTask playTask = new PlayTask();
        playTask.execute();
    }

    private void disconnect()
    {
        DisconnectTask disconnectTask = new DisconnectTask();
        disconnectTask.execute();
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

    private class PlayTask extends AsyncTask<Object, Object, MpdPlayerAdapterIF.PlayStatus>
    {
        @Override
        protected MpdPlayerAdapterIF.PlayStatus doInBackground(Object... objects)
        {
            return mpdPlayerAdapterIF.playOrPause();
        }

        @Override
        protected void onPostExecute(MpdPlayerAdapterIF.PlayStatus playStatus)
        {
            Log.v(TAG, "Play Status Update: " + playStatus);
            updatePlayStatusOnUI(playStatus);
        }

    }

    private class NextTask extends AsyncTask<Object, Object, Object>
    {
        @Override
        protected Object doInBackground(Object... objects)
        {
            mpdPlayerAdapterIF.next();
            return null;
        }
    }

    private class VolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        public void onProgressChanged(SeekBar seekBar, int volume, boolean fromUser)
        {
            if (seekBar == volumeSeekBar && fromUser)
            {
                VolumeTask volumeTask = new VolumeTask();
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

    private class VolumeTask extends AsyncTask<Integer, Object, Integer>
    {
        @Override
        protected Integer doInBackground(Integer... params)
        {
            Integer volume = params[0];
            return mpdPlayerAdapterIF.setVolume(volume);
        }
    }

    private class ToggleMuteTask extends AsyncTask<Object, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(Object... objects)
        {
            Boolean muted = mpdPlayerAdapterIF.toggleMute();
            Integer volume = mpdPlayerAdapterIF.getVolume();
            publishProgress(volume);
            return muted;
        }

        @Override
        protected void onPostExecute(Boolean muted)
        {
            muteButton.setSelected(muted);
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            Integer volume = values[0];
            volumeSeekBar.setProgress(volume);
        }
    }
}
