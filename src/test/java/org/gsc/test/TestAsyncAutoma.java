package org.gsc.test;

import org.gsc.automa.AsyncAutoma;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaState;
import org.gsc.test.utils.AutomaTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.gsc.automa.StateConnector.from;

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
    private AutomaState start;
    private AutomaState end;
    private AutomaEvent event;
    private SpyAction action;
    private AsyncAutoma automa;

    @Before
    public void before() {
        super.before();
        execService = Executors.newFixedThreadPool(1);
        start = nextState();
        end = nextState();
        event = nextEvent();
        action = new SpyAction();
        automa = new AsyncAutoma(execService, start);
    }

    @After
    public void after() {
        super.after();
        execService.shutdownNow();
    }

    @Test
    public void shouldExecuteActionOnSeparateThread() throws Exception {
        // setup
        from(start).stay().when(event).andDo(action);
        // exercise
        automa.signalEvent(event);
        // verify
        action.assertExecutedOnDifferentThread(Thread.currentThread());
    }

}

