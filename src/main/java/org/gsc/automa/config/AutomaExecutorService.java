package org.gsc.automa.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 9.02
 * To change this template use File | Settings | File Templates.
 */
public class AutomaExecutorService implements IAutomaExecutorService {
    private ExecutorService service;

    public AutomaExecutorService() {
        service = Executors.newFixedThreadPool(AutomaConfiguration.getThreadsNumber());
    }

    @Override
    public void submitJob(Runnable job) {
        service.submit(job);
    }

    @Override
    public void stopService() {
        service.shutdownNow();
    }
}
