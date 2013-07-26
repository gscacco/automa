package org.gsc.automa.config;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 9.00
 * To change this template use File | Settings | File Templates.
 */
public class AutomaServiceDiscovery {
    private static IOutputStreamService outputStreamService;

    public static void setOutputStreamService(IOutputStreamService outputStreamService) {
        AutomaServiceDiscovery.outputStreamService = outputStreamService;
    }

    public static IOutputStreamService getOutputStreamService() {
        return outputStreamService;
    }
}
