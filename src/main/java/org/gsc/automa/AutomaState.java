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

public class AutomaState<STATE extends Enum, EVENT extends Enum> {

    private STATE state;
    private HashMap<Integer, Transition<STATE>> transitions = new HashMap<Integer, Transition<STATE>>();

    public AutomaState(STATE state) {
        this.state = state;
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
        if (transitions.containsKey(event.ordinal())) {
            throw new RuntimeException("The transition already exists");
        }
        transitions.put(event.ordinal(), new Transition(state,
                endState,
                action,
                validator));
    }

}
