package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.MpdServer;
import com.bender.mpdroid.simulator.MpdServerSimulator;

/**
 * todo: replace with documentation
 */
public class SimulatedMpdServerAdapter extends MpdLibServiceAdapter
{
    public SimulatedMpdServerAdapter()
    {
        super();
        MpdServerSimulator mpdServerSimulator = new MpdServerSimulator();
        MpdServer mpdServer = new MpdServer(mpdServerSimulator.createMpdSocket(), mpdServerSimulator.createMpdSocket());
        setMpdServer(mpdServer);
    }
}
