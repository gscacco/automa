/*
 * Copyright 2013 Gianluca Scacco & Raffaele Rossi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Gianluca Scacco <gianluca.scacco@gmail.com>
 * Raffaele Rossi <rossi.raffaele@gmail.com>
 */
package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.Choice;
import org.gsc.automa.ChoicePoint;
import org.gsc.automa.EventValidator;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.gsc.test.utils.SpyTransitionHookAction;
import org.junit.Before;
import org.junit.Test;

public class TestAutoma extends AutomaTestCase {

    private Automa automa;
    private SpyAction action;
    private int actionsExecutionOrder;

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
    public void shouldIgnoreUnmappedEvent() {
        automa.signalEvent(FakeEvent.EVENT_1);
    }

    @Test
    public void shouldPassPayloadToAction() {
        class MyAction implements Automa.Action {
            public boolean executed = false;
            public Object payload;

            @Override
            public void run(Object o) {
                executed = true;
                payload = o;
            }

            public void assertPayload(Object o) {
                assertTrue("Action not executed", executed);
                assertEquals("Payload", o, payload);
            }
        }
        // setup
        Object payload = new Object();
        MyAction spyAction = new MyAction();
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDo(spyAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1, payload);
        // verify
        spyAction.assertPayload(payload);
    }

    @Test
    public void shouldHandleNestedSignal() {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                automa.signalEvent(FakeEvent.EVENT_2);
            }
        };
        // setup
        SpyAction spyAction = new SpyAction();
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(action);
        automa.from(FakeState.STATE_2).goTo(FakeState.STATE_1).when(FakeEvent.EVENT_2).andDo(spyAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        spyAction.assertExecuted();
    }

    @Test
    public void shouldExecuteActionsInOrder() {
        class CountingAction implements Runnable {
            int position;

            @Override
            public void run() {
                position = ++actionsExecutionOrder;
            }
        }
        // setup
        CountingAction exitAction = new CountingAction();
        CountingAction action = new CountingAction();
        CountingAction entryAction = new CountingAction();

        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(action);
        automa.onceOut(FakeState.STATE_1, exitAction);
        automa.onceIn(FakeState.STATE_2, entryAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        assertEquals("Entry action not executed as 1st", 1, exitAction.position);
        assertEquals("Transiion action not executed as 2nd", 2, action.position);
        assertEquals("Exit action not executed as 3rd", 3, entryAction.position);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionOnTransitionRewrite() {
        //setup
        EventValidator alwaysTrue = new SimpleValidator(true);
        EventValidator alwaysFalse = new SimpleValidator(false);

        SpyAction secondAction = new SpyAction();

        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_3).when(FakeEvent.EVENT_1).onlyIf(alwaysTrue).andDo(action);
        //exercise
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).onlyIf(alwaysFalse).andDo(secondAction);
        //verify
    }

    @Test
    public void shouldExecutePostTransition() {
        //setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDoNothing();
        SpyTransitionHookAction postRun = new SpyTransitionHookAction();
        automa.setPostTransitionHook(postRun);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        postRun.assertExecuted();
        assertEquals(postRun.getFromStatus(), FakeState.STATE_1);
        assertEquals(postRun.getToStatus(), FakeState.STATE_2);
    }

    @Test
    public void shouldExecutePostTransitionOnChoice() {
        //setup
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return Choice.doNothingAndStay();
            }
        }).when(FakeEvent.EVENT_1);
        SpyTransitionHookAction postRun = new SpyTransitionHookAction();
        automa.setPostTransitionHook(postRun);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        postRun.assertExecuted();
    }

    @Test
    public void shouldAutomaResetAndDo() {
        //setup
        automa.reset().when(FakeEvent.EVENT_1).andDo(action);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        action.assertExecuted();
    }

    @Test
    public void shouldAutomaResetAndDoNothing() {
        //setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_2).andDo(action);
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_3).when(FakeEvent.EVENT_3).andDoNothing();
        automa.reset().when(FakeEvent.EVENT_1).andDoNothing();
        //exercise
        automa.signalEvent(FakeEvent.EVENT_3);

        automa.signalEvent(FakeEvent.EVENT_1);

        automa.signalEvent(FakeEvent.EVENT_2);
        //verify
        action.assertExecuted();
    }

    @Test
    public void shouldAutomaResetAndCheckWithPostTransitionHook() {
        //setup
        SpyTransitionHookAction postRun = new SpyTransitionHookAction();
        automa.setPostTransitionHook(postRun);
        automa.reset().when(FakeEvent.EVENT_1).andDoNothing();
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        postRun.assertExecuted();
    }

    @Test
    public void shouldExecuteExitActionOnReset() {
        //setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDoNothing();
        automa.onceOut(FakeState.STATE_2, action);
        automa.reset().when(FakeEvent.EVENT_2).andDoNothing();
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_2);
        //verify
        action.assertExecuted();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenOverwriteOnReset() {
        //setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDoNothing();
        automa.reset().when(FakeEvent.EVENT_1).andDoNothing();
        //exercise
        //verify
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenOverwriteOnResetSecondCase() {
        //setup
        automa.reset().when(FakeEvent.EVENT_1).andDoNothing();
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDoNothing();
        //exercise
        //verify
    }

    private class SimpleValidator implements EventValidator {
        private boolean value;

        public SimpleValidator(boolean value) {
            this.value = value;
        }

        @Override
        public boolean validate(Object object) {
            return value;
        }
    }
}

