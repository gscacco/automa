package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.junit.Before;
import org.junit.Test;

public class TestChildAutoma extends AutomaTestCase {
 
    enum STATE {
        START,
        END
    }
 
    enum START_SUBSTATE {
        LOADING,
        RUNNING
    }

    /**
     * This test needs to be implemented. 
     */
    @Test
    public void shouldExecuteChildAutomaAction() {
        // setup
        Automa automa = new Automa(STATE.START);
        final SpyAction action = new SpyAction();
        // exercise
        automa.addChildAutoma(
            STATE.START,
            new Automa(START_SUBSTATE.LOADING) {{
                from(START_SUBSTATE.LOADING).goTo(START_SUBSTATE.RUNNING).when(FakeEvent.EVENT_1).andDo(action);
            }});
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        action.assertExecuted();
    }
}