package com.bender.mpdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * todo: replace with documentation
 */
public class MpDroidActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = MpDroidActivity.class.getSimpleName();
    private static final int REQUEST_PREFERENCES = 1;

    private Button preferencesButton;
    private TextView serverTextView;
    private TextView portTextView;
    private CheckBox useAuthenticationCheckbox;
    private Button connectButton;
    private MpdPreferences myPreferences;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        myPreferences = new MpdPreferences(this);
        initializeWidgets();
        initializeListeners();
        updatePreferences();
        Log.v(TAG, "onCreate: DONE");
    }

    private void initializeWidgets()
    {
        preferencesButton = (Button) findViewById(R.id.preference);
        serverTextView = (TextView) findViewById(R.id.server_name);
        portTextView = (TextView) findViewById(R.id.port);
        useAuthenticationCheckbox = (CheckBox) findViewById(R.id.use_authentication);
        connectButton = (Button) findViewById(R.id.connect);
    }

    private void initializeListeners()
    {
        preferencesButton.setOnClickListener(this);
        connectButton.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if (view == preferencesButton)
        {
            Intent launchPreferencesIntent = new Intent();
            launchPreferencesIntent.setClass(this, MpDroidPreferenceActivity.class);
            startActivityForResult(launchPreferencesIntent, REQUEST_PREFERENCES);
        } else if (view == connectButton)
        {
            connect();
        }
    }

    //todo: wrap in a thread
    private void connect()
    {
        MpdAdapter mpdAdapter = new MpdAdapter();
        String server = myPreferences.getServer();
        int port = myPreferences.getPort();
        boolean useAuthentication = myPreferences.useAuthentication();
        if (useAuthentication)
        {
            String password = myPreferences.getPassword();
            mpdAdapter.connect(server, port, password);
        } else
        {
            mpdAdapter.connect(server);
        }
        boolean connected = mpdAdapter.isConnected();
        String connectedText = connected ? "Connected to " + server + "!"
                : "Unable to Connect to " + server + "!";
        Log.v(TAG, connectedText);
        Toast.makeText(this, connectedText, Toast.LENGTH_SHORT);
        mpdAdapter.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_PREFERENCES)
        {
            updatePreferences();
        }
    }

    private void updatePreferences()
    {
        String server = myPreferences.getServer();
        serverTextView.setText(getText(R.string.server) + ": " + server);
        String port = String.valueOf(myPreferences.getDefaultPort());
        portTextView.setText(getText(R.string.port) + ": " + port);
        boolean useAuthentication = myPreferences.useAuthentication();
        useAuthenticationCheckbox.setChecked(useAuthentication);
    }
}
