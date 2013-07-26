package org.gsc.test;
import java.io.StringWriter;

import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaState;
import static org.gsc.automa.StateConnector.from;
import org.gsc.automa.UmlWriterAutoma;
import org.gsc.test.utils.AutomaTestCase;
import org.junit.Before;
import org.junit.Test;

public class TestUmlWriterAutoma extends AutomaTestCase {

  @Test
  public void shouldWriteUml() {
    // setup
    AutomaState start = nextState();
    AutomaState middle = nextState();
    AutomaState end = nextState();
    AutomaEvent event1 = nextEvent();
    AutomaEvent event2 = nextEvent();
    from(start).goTo(middle).when(event1).andDoNothing();
    from(middle).goTo(end).when(event2).andDoNothing();
    StringWriter uml = new StringWriter();
    // exercise
    UmlWriterAutoma automa = new UmlWriterAutoma(start, uml);
    automa.signalEvent(event1);
    automa.signalEvent(event2);
    automa.closeAutoma();
    // verify
    StringBuffer expectedUml = new StringBuffer();
    expectedUml.append("@startuml\n");
    expectedUml.append(String.format("%s -> %s : %s\n", start, middle, event1));
    expectedUml.append(String.format("%s -> %s : %s\n", middle, end, event2));
    expectedUml.append("@enduml");
    assertEquals("UML", expectedUml.toString(), uml.toString());
  }

}
