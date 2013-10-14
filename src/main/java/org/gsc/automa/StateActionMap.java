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

public class StateActionMap<STATE extends Enum> extends HashMap<Integer, Automa.Action> {

    public void put(STATE s, Automa.Action r) {
        put(s.ordinal(), r);
    }

    public void runAction(STATE s, Object payload) {
        Automa.Action r = get(s.ordinal());
        if (r != null) {
            r.run(payload);
        }
    }

}
