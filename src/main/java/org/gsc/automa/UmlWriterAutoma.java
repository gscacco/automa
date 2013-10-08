/*
 * Copyright 2013 Gianluca Scacco
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
 */
package org.gsc.automa;

import java.io.IOException;
import java.io.Writer;

public class UmlWriterAutoma<STATE extends Enum, EVENT extends Enum> extends Automa<STATE, EVENT> {

    private Writer _writer;

    public UmlWriterAutoma(STATE startState, Writer writer) {
        super(startState);
        _writer = writer;
        try {
            _writer.write("@startuml\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Intercpet the parent transit method to write the transition
     * to the underlying writer.
     */
    @Override
    protected void transit(Transition<STATE> transition, EVENT event, Object payload) {
        try {
            _writer.write(String.format("%s -> %s : %s\n",
                    currentState,
                    transition.getEndState(),
                    event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.transit(transition, event, payload);
    }

    /**
     * Close the UML generation process by appending the @enduml
     * string to the output writer.
     */
    public void closeAutoma() {
        try {
            _writer.write("@enduml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
