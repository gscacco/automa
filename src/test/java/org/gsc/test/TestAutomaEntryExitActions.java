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
import org.gsc.test.utils.SpyAction;
import org.gsc.test.utils.SpyActionWithPayload;
import org.junit.Before;
import org.junit.Test;


public class TestAutomaEntryExitActions {
    private enum States {
        RUNNING, IDLE
    }

    private enum Events {
        EVENT_ONE

    }

    private Automa<States, Events> automa;
    private SpyAction action;
    private SpyActionWithPayload actionWithPayload;

    @Before
    public void before() {
        automa = new Automa<States, Events>(States.IDLE);
        action = new SpyAction();
        actionWithPayload = new SpyActionWithPayload();
    }

    @Test
    public void shouldHandleEntryAction() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.RUNNING, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldHandleEntryActionWithPayload() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.RUNNING, actionWithPayload);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        actionWithPayload.assertExecuted();
    }

    @Test
    public void shouldIgnoreEntryActionWhenStay() {
        // setup
        automa.from(States.IDLE).stay().when(Events.EVENT_ONE).andDoNothing();
        automa.onceIn(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertNotExecuted();
    }

    @Test
    public void shouldHandleExitAction() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceOut(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertExecuted();
    }

    @Test
    public void shouldHandleExitActionWithPayload() {
        // setup
        automa.from(States.IDLE).goTo(States.RUNNING).when(Events.EVENT_ONE).andDoNothing();
        automa.onceOut(States.IDLE, actionWithPayload);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        actionWithPayload.assertExecuted();
    }

    @Test
    public void shouldIgnoreExitActionWhenStay() {
        // setup
        automa.from(States.IDLE).stay().when(Events.EVENT_ONE).andDoNothing();
        automa.onceOut(States.IDLE, action);
        // exercise
        automa.signalEvent(Events.EVENT_ONE);
        // verify
        action.assertNotExecuted();
    }


}
