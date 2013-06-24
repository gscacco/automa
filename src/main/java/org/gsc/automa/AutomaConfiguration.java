package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 24/06/13
 * Time: 11.30
 * To change this template use File | Settings | File Templates.
 */
public class AutomaConfiguration {
    private static boolean threading = true;

    public static void setThreading(boolean threading) {
        AutomaConfiguration.threading = threading;
    }

    public static boolean isThreading() {
        return threading;
    }
}
