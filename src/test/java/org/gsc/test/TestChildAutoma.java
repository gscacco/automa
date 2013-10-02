package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.HoldingStrategy;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.junit.Before;
import org.junit.Test;

public class TestChildAutoma extends AutomaTestCase {

    private Automa automa;
    private SpyAction childAction;
    private SpyAction parentAction;

    enum State {
        START,
        END
    }

    enum StartSubState {
        LOADING,
        RUNNING
    }

    @Before
    public void before() {
        automa = new Automa(State.START);
        childAction = new SpyAction();
        parentAction = new SpyAction();
        automa.addChildAutoma(
                State.START,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(childAction);
                }});
    }


    @Test
    public void shouldExecuteChildAutomaAction() {
        // setup
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        childAction.assertExecuted();
    }

    @Test
    public void shouldTransitJustTheParent() {
        // setup
        automa.from(State.START).goTo(State.END).when(FakeEvent.EVENT_1).andDo(parentAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        childAction.assertNotExecuted();
        parentAction.assertExecuted();
    }

    @Test
    public void shouldGivePriorityToParent() {
        // setup
        automa.from(State.START).stay().when(FakeEvent.EVENT_1).andDo(parentAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        childAction.assertNotExecuted();
        parentAction.assertExecuted();
    }

    @Test
    public void shouldResetChildAutoma() {
        // setup
        automa.addChildAutoma(
                State.START,
                HoldingStrategy.RESET,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(childAction);
                }});
        automa.from(State.START).goTo(State.END).when(FakeEvent.EVENT_2).andDoNothing();
        automa.from(State.END).goTo(State.START).when(FakeEvent.EVENT_2).andDoNothing();

        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_2);
        automa.signalEvent(FakeEvent.EVENT_2);
        //The sub-automa should be on LOADING
        automa.signalEvent(FakeEvent.EVENT_1);

        //Verify
        childAction.assertExecuted(2);
    }

    @Test
    public void shouldNotResetChildAutomaWhenStay() {
        // setup
        automa.addChildAutoma(
                State.START,
                HoldingStrategy.RESET,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(childAction);
                }});
        automa.from(State.START).stay().when(FakeEvent.EVENT_2).andDoNothing();

        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_2);
        //The sub-automa should be on LOADING
        automa.signalEvent(FakeEvent.EVENT_1);

        //Verify
        childAction.assertExecuted(1);
    }
}