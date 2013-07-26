package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaState;
import org.gsc.automa.EventValidator;
import static org.gsc.automa.StateConnector.from;
import org.gsc.test.utils.AutomaTestCase;
import org.junit.After;
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

        @Override public void run() { numOfExecution++; }

        public void assertExecuted() {
            assertTrue("Action not executed", numOfExecution > 0);
        }

        public void assertExecuted(int expectedNumber) {
            assertEquals("Number of executions", expectedNumber, numOfExecution);
        }
    }

    private Automa automa;
    private AutomaEvent evtOne;
    private AutomaEvent evtTwo;
    private AutomaState start;
    private AutomaState end;
    private SpyAction action;

    @Before
    public void before() {
        super.before();
        evtOne = nextEvent();
        evtTwo = nextEvent();
        start = nextState();
        end = nextState();
        action = new SpyAction();
        automa = new Automa(start);
    }

    @Test
    public void shouldExecuteAction() throws Exception {
        // setup
        from(start).goTo(end).when(evtOne).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldChangeState() throws Exception {
        // setup
        AutomaState middle = nextState();
        from(start).goTo(middle).when(evtOne).andDoNothing();
        from(middle).goTo(end).when(evtOne).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        automa.signalEvent(evtOne);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldStayOnSameState() {
        // setup
        from(start).stay().when(evtOne).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        automa.signalEvent(evtOne);
        // verify
        action.assertExecuted(2);
    }

    @Test
    public void shouldDoTheSameActionForMultipleEvents() {
        // setup
        from(start).stay().forEach(new AutomaEvent[]{evtOne, evtTwo}).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        automa.signalEvent(evtTwo);
        // verify
        action.assertExecuted(2);
    }

    @Test
    public void shouldTransitIfEventObjectIsValid() {
        // setup
        EventValidator validator = new EventValidator() {
            @Override public boolean validate(Object object) { return true; }
        };
        from(start).stay().when(evtOne).onlyIf(validator).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldNotTransitIfEventObjectIsNotValid() {
        // setup
        EventValidator validator = new EventValidator() {
            @Override public boolean validate(Object object) { return false; }
        };
        from(start).stay().when(evtOne).onlyIf(validator).andDo(action);
        // exercise
        automa.signalEvent(evtOne);
        // verify
        action.assertExecuted(0);
    }
}
