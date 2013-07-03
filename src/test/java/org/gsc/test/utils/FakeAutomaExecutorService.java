package org.gsc.test.utils;

import org.gsc.automa.config.AutomaConfiguration;
import org.gsc.automa.config.IAutomaExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 9.05
 * To change this template use File | Settings | File Templates.
 */
public class FakeAutomaExecutorService implements IAutomaExecutorService {
    private final int threads;
    private int numJobs = 0;

    public FakeAutomaExecutorService() {
        threads = AutomaConfiguration.getThreadsNumber();
    }

    public int getThreadsNumber() {
        return threads;
    }

    public int getSubmittedJobs() {
        return numJobs;
    }

    @Override
    public void submitJob(Runnable job) {
        this.numJobs++;
    }

    @Override
    public void stopService() {

    }
}
