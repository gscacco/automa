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
package org.gsc.test;

import org.gsc.automa.UmlWriterAutoma;
import org.gsc.test.utils.AutomaTestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TestUmlWriterAutoma extends AutomaTestCase {

    private class FailingWriter extends Writer {
        public boolean fail = false;

        @Override
        public void write(char cbuf[], int off, int len) throws IOException {
            if (fail) {
                throw new IOException("Test IOException");
            }
        }

        @Override
        public void close() {
            fail("This method should not be invoked");
        }

        @Override
        public void flush() {
            fail("This method should not be invoked");
        }
    }

    @Test
    public void shouldWriteUml() {
        // setup
        StringWriter uml = new StringWriter();
        UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1, uml);
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2)
                .when(FakeEvent.EVENT_1).andDoNothing();
        automa.from(FakeState.STATE_2).goTo(FakeState.STATE_3)
                .when(FakeEvent.EVENT_2).andDoNothing();
        // exercise
        automa.signalEvent(FakeEvent.EVENT_1);
        automa.signalEvent(FakeEvent.EVENT_2);
        automa.closeAutoma();
        // verify
        StringBuffer expectedUml = new StringBuffer();
        expectedUml.append("@startuml\n");
        expectedUml.append(String.format("%s -> %s : %s\n",
                FakeState.STATE_1,
                FakeState.STATE_2,
                FakeEvent.EVENT_1));
        expectedUml.append(String.format("%s -> %s : %s\n",
                FakeState.STATE_2,
                FakeState.STATE_3,
                FakeEvent.EVENT_2));
        expectedUml.append("@enduml");
        assertEquals("UML", expectedUml.toString(), uml.toString());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionOnIOExceptionInTheConstructor() {
        // setup
        FailingWriter writer = new FailingWriter();
        // exercise
        writer.fail = true;
        new UmlWriterAutoma(FakeState.STATE_1, writer);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionOnIOExceptionWhileTransiting() {
        // setup
        FailingWriter writer = new FailingWriter();
        UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1, writer);
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2)
                .when(FakeEvent.EVENT_1).andDoNothing();
        // exercise
        writer.fail = true;
        automa.signalEvent(FakeEvent.EVENT_1);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionOnIOExceptionWhileClosing() {
        // setup
        FailingWriter writer = new FailingWriter();
        UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1, writer);
        automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2)
                .when(FakeEvent.EVENT_1).andDoNothing();
        automa.signalEvent(FakeEvent.EVENT_1);
        // exercise
        writer.fail = true;
        automa.closeAutoma();
    }
}
