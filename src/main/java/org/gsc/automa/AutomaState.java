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

import java.util.HashMap;
import java.util.Map;

public class AutomaState<STATE extends Enum, EVENT extends Enum> {

    private STATE state;
    private HashMap<Integer, Transition<STATE>> transitions = new HashMap<Integer, Transition<STATE>>();
    private Map<EVENT, ChoicePoint> choicePointMap = new HashMap<EVENT, ChoicePoint>();

    private Automa.Action entryAction;
    private Automa.Action exitAction;

    static private final Automa.Action NULL_ACTION = new Automa.Action() {
        @Override
        public void run(Object payload) { /* do nothing */ }
    };

    public AutomaState(STATE state) {
        this.state = state;
        entryAction = NULL_ACTION;
        exitAction = NULL_ACTION;
    }

    public STATE getState() {
        return state;
    }

    public Transition getTransition(EVENT event) {
        return transitions.get(event.ordinal());
    }

    public void transitTo(STATE endState,
                          EVENT event,
                          EventValidator validator,
                          Automa.Action action) {
        if (transitions.containsKey(event.ordinal()) || choicePointMap.containsKey(event)) {
            throw new RuntimeException("The transition already exists");
        }
        transitions.put(event.ordinal(), new Transition(state,
                endState,
                action,
                validator));
    }

    void setEntryAction(Automa.Action action) {
        this.entryAction = action;
    }

    void setExitAction(Automa.Action action) {
        this.exitAction = action;
    }

    public void execEntryAction(Object payload) {
        entryAction.run(payload);
    }

    public void execExitAction(Object payload) {
        exitAction.run(payload);
    }

    public void setChoicePoint(ChoicePoint choicePoint, EVENT choicePointEvent) {
        //There is already a choice with the same event
        if (this.choicePointMap.containsKey(choicePointEvent)) {
            throw new RuntimeException("The choice point already exists");
        }
        this.choicePointMap.put(choicePointEvent, choicePoint);
    }

    public ChoicePoint getChoicePoint(EVENT event) {
        return choicePointMap.get(event);
    }
}
