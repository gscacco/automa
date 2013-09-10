package org.gsc.test;

import junit.framework.Assert;
import org.gsc.automa.Automa;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: G.Scacco
 * Date: 10/09/13
 * Time: 13.59
 */
public class TestAutomaEntryExitActions {
    private enum States {
        RUNNING, IDLE
    }

    private enum Events {
        EVENT_TWO, EVENT_ONE

    }

    private class SpyAction implements Runnable {
        private int numExecuted = 0;

        @Override
        public void run() {
            numExecuted++;
        }

        public void assertExecuted(int num) {
            Assert.assertTrue("Action executed", numExecuted == num);
        }
    }

    private Automa<States, Events> automa;

    @Before
    public void before() {
        automa = new Automa<States, Events>(States.IDLE);
    }

    @Test
    public void shouldHandleEntryAction() {
        SpyAction action = new SpyAction();
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.RUNNING).executeAction(action);

        automa.signalEvent(Events.EVENT_ONE);

        action.assertExecuted(1);
    }

    @Test
    public void shouldHandleEntryActionWhenStay() {
        SpyAction action = new SpyAction();
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.from(States.RUNNING).stay().when(Events.EVENT_TWO).andDoNothing();
        automa.onceIn(States.RUNNING).executeAction(action);

        automa.signalEvent(Events.EVENT_ONE);
        automa.signalEvent(Events.EVENT_TWO);
        action.assertExecuted(1);
    }

    @Test
    public void shouldHandleExitAction() {
        SpyAction action = new SpyAction();
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.from(States.RUNNING).goTo(States.IDLE).when(Events.EVENT_TWO).andDoNothing();
        automa.onceOut(States.RUNNING).executeAction(action);

        automa.signalEvent(Events.EVENT_ONE);
        automa.signalEvent(Events.EVENT_TWO);

        action.assertExecuted(1);
    }

}
