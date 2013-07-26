package org.gsc.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.gsc.automa.AsyncAutoma;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaFactory;
import org.gsc.automa.AutomaState;
import static org.gsc.automa.StateConnector.from;
import org.gsc.automa.config.AutomaServiceDiscovery;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.FakeFileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the AsyncAutoma
 * 
 * @author raffaelerossi (7/24/2013)
 */
public class TestAsyncAutoma extends AutomaTestCase {

    private class SpyAction implements Runnable {
        private long tid = 0;
        @Override
        synchronized public void run() {
          tid = Thread.currentThread().getId();
          notify();
        }
        synchronized public void assertExecutedOnDifferentThread(Thread t)
        throws Exception {
          wait(500);
          assertTrue("Action not executed", tid != 0);
          assertTrue("Action TID equals test one", tid != t.getId());
        }
    }

    private ExecutorService execService;

    @Before
    public void before() {
        super.setUp();
        execService = Executors.newFixedThreadPool(1);
    }

    @After
    public void after() {
      super.tearDown();
      execService.shutdownNow();
    }

    @Test
    public void shouldRunActionOnSeparateThread() throws Exception {
        // setup
        AutomaState start = nextState();
        AutomaEvent event = nextEvent();
        SpyAction action = new SpyAction();
        from(start).stay().when(event).andDo(action);
        AsyncAutoma automa = new AsyncAutoma(execService, start);
        // exercise
        automa.signalEvent(event);
        // verify
        action.assertExecutedOnDifferentThread(Thread.currentThread());
    }

}
