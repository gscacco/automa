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
package org.gsc.automa;

import java.util.ArrayList;

public class StateConnector<STATE extends Enum, EVENT extends Enum> {
    private AutomaState<STATE, EVENT> startState;
    private STATE nextState;
    private ArrayList<EVENT> events;
    private Automa.Action action = null;
    private EventValidator validator;

    static private Automa.Action nullAction = new Automa.Action() {
        @Override
        public void run(Object obj) { /* Do nothing */ }
    };

    static private EventValidator nullValidator = new EventValidator() {
        @Override
        public boolean validate(Object obj) {
            return true;
        }
    };
    private ChoiceConnector choiceConnector;

    public StateConnector(AutomaState<STATE, EVENT> startState) {
        this.startState = startState;
        this.nextState = startState.getState();
        this.events = new ArrayList<EVENT>();
        this.action = nullAction;
        this.validator = nullValidator;
    }

    public StateConnector goTo(STATE state) {
        this.nextState = state;
        return this;
    }

    public StateConnector when(EVENT event) {
        this.events.add(event);
        return this;
    }

    public void andDo(Runnable action) {
        andDo(new RunnableActionAdapter(action));
    }

    public void andDo(Automa.Action action) {
        this.action = action;
        for (EVENT event : this.events) {
            startState.transitTo(nextState, event, validator, action);
        }

    }

    public void andDoNothing() {
        andDo(this.action);
    }

    public StateConnector stay() {
        nextState = startState.getState();
        return this;
    }

    public StateConnector forEach(EVENT... events) {
        for (EVENT event : events) {
            this.events.add(event);
        }
        return this;
    }

    public StateConnector onlyIf(EventValidator validator) {
        this.validator = validator;
        return this;
    }

    public ChoiceConnector choice(ChoicePoint choicePoint) {
        this.choiceConnector = new ChoiceConnector(startState, choicePoint);
        return choiceConnector;
    }

    public class ChoiceConnector {
        private final AutomaState state;
        private final ChoicePoint choice;
        private EVENT event;

        public ChoiceConnector(AutomaState state, ChoicePoint choice) {
            this.state = state;
            this.choice = choice;
        }

        public void when(EVENT event) {
            this.event = event;
            state.setChoicePoint(choice, event);
        }
    }
}
