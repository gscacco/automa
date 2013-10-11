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
import org.gsc.automa.EventValidator;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.SpyAction;
import org.junit.Before;
import org.junit.Test;

public class TestAutoma extends AutomaTestCase {

    private Automa automa;
    private SpyAction action;
    private int seqNum=1;
    private int res=0;

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
        
        Runnable exitAction = new Runnable() {
            @Override
            public void run() {
                res+=Math.pow(2,seqNum++);
            }
        };
        Runnable action = new Runnable() {
            @Override
            public void run() {
                res+=Math.pow(3,seqNum++);
            }
        };
        Runnable entryAction = new Runnable() {
            @Override
            public void run() {
                res+=Math.pow(5,seqNum++);
            }
        };
        
        // setup
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2).when(FakeEvent.EVENT_1).andDo(action);
        automa.onceOut(FakeState.STATE_1, exitAction);
        automa.onceIn(FakeState.STATE_2, entryAction);
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        // verify
        assertEquals((int)(Math.pow(2,1)+Math.pow(3,2)+Math.pow(5,3)), res);
    }
}


