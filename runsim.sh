#!/bin/sh

HOME=`dirname $0`

CLASSPATH="$HOME/out/test/mpdlibrary"
CLASSPATH="$CLASSPATH:$HOME/out/production/mpdlibrary"
CLASSPATH="$CLASSPATH:$HOME/mpdlibrary/lib/library-schema.jar"
CLASSPATH="$CLASSPATH:$HOME/mpdlibrary/lib/xbean.jar"

java -cp $CLASSPATH com.bender.mpdlib.simulator.MpdServerSimulator $*
