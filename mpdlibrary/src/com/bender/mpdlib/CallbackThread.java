package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;
import com.bender.mpdlib.util.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

/**
 */
class CallbackThread extends Thread
{
    private SocketAddress address;
    private volatile boolean disconnected;
    private MpdServer mpdServer;
    private static final String TAG = CallbackThread.class.getSimpleName();
    private Pipe callbackPipe;

    public CallbackThread(MpdServer mpdServer, SocketAddress theAddress, Pipe callbackPipe)
    {
        super(TAG);
        this.callbackPipe = callbackPipe;
        this.mpdServer = mpdServer;
        address = theAddress;
    }

    public void run()
    {
        try
        {
            Result<String> connectResult = CommandRunner.runCommand(new ConnectCommand(callbackPipe, address));
            if (!connectResult.status.isSuccessful())
            {
                Log.w(TAG, "connect unsuccessful: " + connectResult.status.getResultString());
                return;
            }
            while (!disconnected)
            {
                Result<List<Subsystem>> idleResult = CommandRunner.runCommand(new IdleCommand(callbackPipe));
                if (disconnected)
                {
                    Log.w(TAG, "CallbackThread disconnected");
                    return;
                }
                if (idleResult.status.isSuccessful())
                {
                    List<Subsystem> result = idleResult.result;
                    if (result.contains(Subsystem.mixer) || result.contains(Subsystem.player))
                    {
                        getAndProcessStatus();
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (!disconnected)
            {
                Log.e(TAG, e);
                try
                {
                    callbackPipe.disconnect();
                }
                catch (IOException e1)
                {
                    Log.e(TAG, e1);
                }
                mpdServer.crashDetected();
            }
        }
        Log.v(TAG, "CallbackThread DONE");
    }

    private void getAndProcessStatus()
    {
        Result<List<StatusTuple>> listResult = CommandRunner.runCommand(new GetStatusCommand(callbackPipe));
        mpdServer.processStatuses(listResult.result);
    }

    @Override
    public void interrupt()
    {
        disconnected = true;
        try
        {
            // Can't use #DisconnectCommand because when unit testing the callback thread is blocked in the simulator.
            callbackPipe.write(MpdCommands.close);
            callbackPipe.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        super.interrupt();
    }
}
