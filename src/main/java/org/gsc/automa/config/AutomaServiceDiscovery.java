package org.gsc.automa.config;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 9.00
 * To change this template use File | Settings | File Templates.
 */
public class AutomaServiceDiscovery {
    private static IAutomaExecutorService executorService;

    public static void setExecutorService(IAutomaExecutorService executorService) {
        AutomaServiceDiscovery.executorService = executorService;
    }

    public static IAutomaExecutorService getExecutorService() {
        return executorService;
    }
}
