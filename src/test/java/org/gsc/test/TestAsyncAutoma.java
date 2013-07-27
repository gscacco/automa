package org.gsc.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gsc.automa.AsyncAutoma;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.AutomaTestCase.FakeEvent;
import org.gsc.test.utils.AutomaTestCase.FakeState;
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
    private SpyAction action;
    private AsyncAutoma automa;

    @Before
    public void before() {
        execService = Executors.newFixedThreadPool(1);
        action = new SpyAction();
        automa = new AsyncAutoma(execService, FakeState.STATE_1, FakeEvent.class);
    }

    @After
    public void after() {
        execService.shutdownNow();
    }

    @Test
    public void shouldExecuteActionOnSeparateThread() throws Exception {
        // setup
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecutedOnDifferentThread(Thread.currentThread());
    }

}

