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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

    private MpdPreferences myPreferences;
    private MpdAdapterIF mpdAdapterIF;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        mpdAdapterIF = MpdAdapterFactory.createAdapter();
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
    }

    private void initializeListeners()
    {
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        connectButton.setOnClickListener(buttonClickListener);
        playButton.setOnClickListener(buttonClickListener);
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
    MpdAdapterIF getMpdAdapterIF()
    {
        return mpdAdapterIF;
    }

    private void updateConnectedStatusOnUI(Boolean connected)
    {
        String text = connected ? getString(R.string.disconnect) : getString(R.string.connect);
        connectButton.setText(text);
        playButton.setEnabled(connected);
    }

    private void updatePlayStatusOnUI(MpdAdapterIF.PlayStatus playStatus)
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

    private class ConnectTask extends AsyncTask<Object, MpdAdapterIF.PlayStatus, Boolean>
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
                mpdAdapterIF.connect(server, port, password);
            } else if (usePort)
            {
                mpdAdapterIF.connect(server, port);
            } else
            {
                mpdAdapterIF.connect(server);
            }
            boolean connected = mpdAdapterIF.isConnected();

            MpdAdapterIF.PlayStatus playStatus = mpdAdapterIF.getPlayStatus();
            publishProgress(playStatus);

            String connectedText = makeConnectedText(server, connected);
            Log.v(TAG, connectedText);
            Log.v(TAG, "MPD Server version: " + mpdAdapterIF.getServerVersion());
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
        protected void onProgressUpdate(MpdAdapterIF.PlayStatus... values)
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
                boolean connected = mpdAdapterIF.isConnected();
                if (connected)
                {
                    disconnect();
                } else
                {
                    connect();
                }
            } else if (view == playButton)
            {
                play();
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
            mpdAdapterIF.disconnect();
            return mpdAdapterIF.isConnected();
        }

        @Override
        protected void onPostExecute(Boolean connected)
        {
            updateConnectedStatusOnUI(connected);
        }
    }

    private class PlayTask extends AsyncTask<Object, Object, MpdAdapterIF.PlayStatus>
    {
        @Override
        protected MpdAdapterIF.PlayStatus doInBackground(Object... objects)
        {
            return mpdAdapterIF.playOrPause();
        }

        @Override
        protected void onPostExecute(MpdAdapterIF.PlayStatus playStatus)
        {
            Log.v(TAG, "Play Status Update: " + playStatus);
            updatePlayStatusOnUI(playStatus);
        }

    }
}
