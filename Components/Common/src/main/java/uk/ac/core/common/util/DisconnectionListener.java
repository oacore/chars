package uk.ac.core.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class DisconnectionListener {

    private static final Logger log = LoggerFactory.getLogger(DisconnectionListener.class);

    public static void halt(Throwable exception) {
        log.error("Caught a runtime exception", exception);
        log.warn("Cannot continue working so I have to restart");
        log.warn("Exiting with exit code 1 ...");
        Runtime.getRuntime().halt(1);
    }

}
