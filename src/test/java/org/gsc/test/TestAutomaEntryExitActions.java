package org.gsc.test;

import junit.framework.Assert;
import org.gsc.automa.Automa;
import org.gsc.test.utils.SpyAction;
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

    private Automa<States, Events> automa;
    private SpyAction action;

    @Before
    public void before() {
        automa = new Automa<States, Events>(States.IDLE);
        action = new SpyAction();
    }

    @Test
    public void shouldHandleEntryAction() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.RUNNING, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldIgnoreEntryActionWhenStay() {
        // setup
        automa.from(States.IDLE).stay().when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertNotExecuted();
    }

    @Test
    public void shouldHandleExitAction() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceOut(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldIgnoreExitActionWhenStay() {
        // setup
        automa.from(States.IDLE).stay().when(Events.EVENT_ONE).andDoNothing();
        automa.onceOut(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertNotExecuted();
    }

}
