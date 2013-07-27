package org.gsc.test;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.gsc.automa.UmlWriterAutoma;
import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.AutomaTestCase.FakeState;
import org.junit.Before;
import org.junit.Test;

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
    UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1,
                                                 FakeEvent.class,
                                                 uml);
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

  @Test(expected=RuntimeException.class)
  public void shouldThrowRuntimeExceptionOnIOExceptionInTheConstructor() {
    // setup
    FailingWriter writer = new FailingWriter();
    // exercise
    writer.fail = true;
    new UmlWriterAutoma(FakeState.STATE_1, FakeEvent.class, writer);
  }

  @Test(expected=RuntimeException.class)
  public void shouldThrowRuntimeExceptionOnIOExceptionWhileTransiting() {
    // setup
    FailingWriter writer = new FailingWriter();
    UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1,
                                                 FakeEvent.class,
                                                 writer);
    automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2)
          .when(FakeEvent.EVENT_1).andDoNothing();
    // exercise
    writer.fail = true;
    automa.signalEvent(FakeEvent.EVENT_1);
  }

  @Test(expected=RuntimeException.class)
  public void shouldThrowRuntimeExceptionOnIOExceptionWhileClosing() {
    // setup
    FailingWriter writer = new FailingWriter();
    UmlWriterAutoma automa = new UmlWriterAutoma(FakeState.STATE_1,
                                                 FakeEvent.class,
                                                 writer);
    automa.from(FakeState.STATE_1).goTo(FakeState.STATE_2)
          .when(FakeEvent.EVENT_1).andDoNothing();
    automa.signalEvent(FakeEvent.EVENT_1);
    // exercise
    writer.fail = true;
    automa.closeAutoma();
  }
}
