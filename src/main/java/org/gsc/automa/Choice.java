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

/**
 * Created by gianluca on 12/02/14.
 */
public class Choice<STATE extends Enum> {
    private static final Automa.Action NULL_ACTION = new Automa.Action() {
        @Override
        public void run(Object payload) {
            /* Do nothing */
        }
    };
    private STATE state;
    private Automa.Action action = NULL_ACTION;

    public Choice(STATE state, Automa.Action action) {
        this.state = state;
        this.action = action;
    }

    public Choice(STATE state, Runnable action) {
        this(state, new RunnableActionAdapter(action));
    }

    public Choice(STATE state) {
        this(state, NULL_ACTION);
    }

    public Choice(Automa.Action action) {
        this(null, action);
    }

    public Choice(Runnable runnable) {
        this(new RunnableActionAdapter(runnable));
    }

    public STATE getState() {
        return state;
    }

    public Automa.Action getAction() {
        return action;
    }

    public static Choice doNothingAndStay() {
        return new Choice(NULL_ACTION);
    }
}
