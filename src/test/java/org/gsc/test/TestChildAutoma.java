package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.junit.Test;

public class TestChildAutoma extends AutomaTestCase {

    enum State {
        START,
        END
    }

    enum StartSubState {
        LOADING,
        RUNNING
    }

    /**
     * This test needs to be implemented.
     */
    @Test
    public void shouldExecuteChildAutomaAction() {
        // setup
        Automa automa = new Automa(State.START);
        final SpyAction action = new SpyAction();
        // exercise
        automa.addChildAutoma(
                State.START,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(action);
                }});
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldGivePriorityToParent() {
        // setup
        Automa automa = new Automa(State.START);
        final SpyAction childAction = new SpyAction();
        SpyAction parentAction = new SpyAction();

        automa.addChildAutoma(
                State.START,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(childAction);
                }});
        automa.from(State.START).goTo(State.END).when(FakeEvent.EVENT_1).andDo(parentAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        childAction.assertNotExecuted();
        parentAction.assertExecuted();
    }

    @Test
    public void shouldGivePriorityToParentOnStay() {
        // setup
        Automa automa = new Automa(State.START);
        final SpyAction childAction = new SpyAction();
        SpyAction parentAction = new SpyAction();

        automa.addChildAutoma(
                State.START,
                new Automa(StartSubState.LOADING) {{
                    from(StartSubState.LOADING).goTo(StartSubState.RUNNING).when(FakeEvent.EVENT_1).andDo(childAction);
                }});
        automa.from(State.START).stay().when(FakeEvent.EVENT_1).andDo(parentAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        childAction.assertNotExecuted();
        parentAction.assertExecuted();
    }
}