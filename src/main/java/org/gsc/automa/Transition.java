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

public class Transition<STATE extends Enum> {
    private final STATE startState;
    private final STATE endState;
    private final Automa.Action action;
    private EventValidator validator;

    public Transition(STATE startState,
                      STATE endState,
                      Automa.Action action,
                      EventValidator validator) {
        this.startState = startState;
        this.endState = endState;
        this.action = action;
        this.validator = validator;
    }

    public Automa.Action getAction() {
        return action;
    }

    public STATE getStartState() {
        return startState;
    }

    public STATE getEndState() {
        return endState;
    }

    public EventValidator getValidator() {
        return validator;
    }

    public boolean isLace() {
        return startState == endState;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("From ").append(startState).append(" to ").append(endState);
        return buffer.toString();
    }
}
