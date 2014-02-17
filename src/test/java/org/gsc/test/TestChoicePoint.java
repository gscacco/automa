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
import org.junit.Before;
import org.junit.Test;

public class TestChoicePoint extends AutomaTestCase {

    private Automa automa;
    private SpyAction action;

    @Before
    public void before() {
        action = new SpyAction();
        automa = new Automa(FakeState.STATE_1);
    }

    @Test
    public void shouldExecuteChosenAction() {
        //setup
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(action);
            }
        }).when(FakeEvent.EVENT_1);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        action.assertExecuted();
    }

    @Test
    public void shouldTransitateToChosenStateDoingNothing() {
        //setup
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_2);
            }
        }).when(FakeEvent.EVENT_1);
        automa.from(FakeState.STATE_2).stay().when(FakeEvent.EVENT_1).andDo(action);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        action.assertExecuted();
    }

    @Test
    public void shouldTransitateToChosenStateAndExecuteChosenAction() {
        //setup
        final SpyAction secondAction = new SpyAction();
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_2, action);
            }
        }).when(FakeEvent.EVENT_1);
        automa.from(FakeState.STATE_2).stay().when(FakeEvent.EVENT_1).andDo(secondAction);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        action.assertExecuted();
        secondAction.assertExecuted();
    }

    @Test(expected = RuntimeException.class)
    public void shouldPreventOverwritingChoicePointWithSimpleTransitions() {
        //setup
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_2);
            }
        }).when(FakeEvent.EVENT_1);
        //exercise
        automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDoNothing();
        //verify
    }

    @Test(expected = RuntimeException.class)
    public void shouldPreventOverwritingChoicePoints() {
        //setup
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_1);
            }
        }).when(FakeEvent.EVENT_1);
        //exercise
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
              return new Choice(FakeState.STATE_2);
            }
        }).when(FakeEvent.EVENT_1);
        //verify
    }


    @Test
    public void shouldHandleMoreChoicePoints() {
        //setup
        final SpyAction secondAction = new SpyAction();
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_2, action);
            }
        }).when(FakeEvent.EVENT_1);
        automa.from(FakeState.STATE_1).choice(new ChoicePoint() {
            @Override
            public Choice choose(Object payload) {
                return new Choice(FakeState.STATE_3, secondAction);
            }
        }).when(FakeEvent.EVENT_2);
        //exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        //verify
        action.assertExecuted();
        secondAction.assertNotExecuted();
    }

}

