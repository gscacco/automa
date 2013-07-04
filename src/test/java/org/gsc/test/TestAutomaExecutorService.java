package org.gsc.test;

import org.gsc.automa.config.AutomaConfiguration;
import org.gsc.automa.config.AutomaExecutorService;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 10.15
 * To change this template use File | Settings | File Templates.
 */
public class TestAutomaExecutorService {
    private boolean actionExecuted = false;
    private Boolean waitVar = false;

    @Before
    public void before() {
        actionExecuted = false;
    }

    @Test
    public void shouldVerifyJobExecution() throws InterruptedException {
        Logger.getAnonymousLogger().info("");
        AutomaConfiguration.setThreadsNumber(1);
        AutomaExecutorService service = new AutomaExecutorService();
        Runnable job = new Runnable() {
            @Override
            public void run() {
                actionExecuted = true;
                synchronized (waitVar) {
                    waitVar.notify();
                }
            }
        };
        service.submitJob(job);
        synchronized (waitVar) {
            waitVar.wait();
        }
        assertTrue(actionExecuted);
    }

    @Test
    public void shouldVerifyServiceStop() throws InterruptedException {
        Logger.getAnonymousLogger().info("");
        AutomaConfiguration.setThreadsNumber(1);
        AutomaExecutorService service = new AutomaExecutorService();
        Runnable job = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                        actionExecuted = true;
                        synchronized (waitVar) {
                            waitVar.notify();
                        }
                    }
                }
            }
        };
        service.submitJob(job);
        service.stopService();
        synchronized (waitVar) {
            waitVar.wait(2000);
            assertTrue(actionExecuted);
        }
    }
}
