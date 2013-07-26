package org.gsc.automa;
import java.io.IOException;
import java.io.Writer;

public class UmlWriterAutoma extends Automa {
  
    private Writer _writer;

    public UmlWriterAutoma(AutomaState startState, Writer writer) {
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
    protected void transit(AutomaState startState, AutomaState endState,
                           Runnable action, AutomaEvent event) {
      try { 
        _writer.write(String.format("%s -> %s : %s\n", 
                                    startState, 
                                    endState,
                                    event));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      super.transit(startState, endState, action, event);
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
