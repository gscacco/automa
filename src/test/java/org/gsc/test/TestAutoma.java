/*
 * Copyright 2013 Gianluca Scacco
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Gianluca Scacco <gianluca.scacco@gmail.com>
 */
package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.EventValidator;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.junit.Before;
import org.junit.Test;


public class TestAutoma extends AutomaTestCase {

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

    @Test
    public void shouldHandleNestedSignal() {
        SpyAction spyAction = new SpyAction();

        Runnable action = new Runnable() {
            @Override
            public void run() {
                automa.signalEvent(FakeEvent.EVENT_2);
            }
        };
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(action);

        automa.from(FakeState.STATE_2).goTo(FakeState.STATE_1).when(FakeEvent.EVENT_2).andDo(spyAction);

        automa.signalEvent(FakeEvent.EVENT_1);
        spyAction.assertExecuted();
    }

}


