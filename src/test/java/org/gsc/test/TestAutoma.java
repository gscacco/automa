package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.EventValidator;
import org.gsc.test.utils.AutomaTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 11/06/13
 * Time: 16.11
 * To change this template use File | Settings | File Templates.
 */
public class TestAutoma extends AutomaTestCase {

    private class SpyAction implements Runnable {
        private int numOfExecution = 0;

        @Override
        public void run() {
            numOfExecution++;
        }

        public void assertExecuted() {
            assertTrue("Action not executed", numOfExecution > 0);
        }

        public void assertExecuted(int expectedNumber) {
            assertEquals("Number of executions", expectedNumber, numOfExecution);
        }


    }

    private Automa automa;
    private SpyAction action;

    @Before
    public void before() {
        action = new SpyAction();
        automa = new Automa(FakeState.STATE_1);
    }

    @Test
    public void shouldExecuteAction() throws Exception {
        // setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldChangeState() throws Exception {
        // setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDoNothing();
        automa.from(FakeState.STATE_2).goTo(FakeState.STATE_3).when(FakeEvent.EVENT_1).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldStayOnSameState() {
        // setup
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted(2);
    }

    @Test
    public void shouldDoTheSameActionForMultipleEvents() {
        // setup
        automa.from(FakeState.STATE_1).stay().forEach(FakeEvent.EVENT_1, FakeEvent.EVENT_2).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_2);
        // verify
        action.assertExecuted(2);
    }

    @Test
    public void shouldTransitIfEventObjectIsValid() {
        // setup
        EventValidator validator = new EventValidator() {
            @Override
            public boolean validate(Object object) {
                return true;
            }
        };
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).onlyIf(validator).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldNotTransitIfEventObjectIsNotValid() {
        // setup
        EventValidator validator = new EventValidator() {
            @Override
            public boolean validate(Object object) {
                return false;
            }
        };
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).onlyIf(validator).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted(0);
    }

    @Test
    public void shouldProvideTheLastEventSignalled() {
        // setup
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDo(action);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        assertEquals("Last event", FakeEvent.EVENT_1, automa.getLastEvent());
    }

    @Test
    public void shouldIgnoreUnmappedEvent() {
        automa.signalEvent(FakeEvent.EVENT_1);
    }


    @Test
    public void shouldRetrievePayload() {
        final Object payload = new Object();

        class MyAction implements Runnable {
            public boolean executed = false;

            @Override
            public void run() {
                executed = true;
                assertEquals(payload, automa.getPayload());
            }
        }
        MyAction testAction = new MyAction();

        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(testAction);
        automa.signalEvent(FakeEvent.EVENT_1, payload);

        assertTrue(testAction.executed);
    }

}


