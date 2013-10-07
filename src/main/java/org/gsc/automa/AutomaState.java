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

    public void transitTo(STATE endState, EVENT event, EventValidator validator, Runnable action) {
        transitions.put(event.ordinal(), new Transition(state,
                endState,
                action,
                validator));
    }

}
