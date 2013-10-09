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