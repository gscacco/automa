package org.gsc.automa.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 24/06/13
 * Time: 11.30
 * To change this template use File | Settings | File Templates.
 */
public class AutomaConfiguration {
    private static boolean threading = true;
    private static int threadsNumber = 1;
    protected static ExecutorService service;

    public static void setThreading(boolean threading) {
        AutomaConfiguration.threading = threading;
    }

    public static boolean isThreading() {
        return threading;
    }

    public static void setThreadsNumber(int threadsNumber) {
        AutomaConfiguration.threadsNumber = threadsNumber;
        service = Executors.newFixedThreadPool(threadsNumber);
    }

    public static int getThreadsNumber() {
        return threadsNumber;
    }
}
