package org.gsc.automa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.43
 * To change this template use File | Settings | File Templates.
 */
public class Automa {
    private AutomaEvent lastEvent;
    private AutomaState currentState;
    private FileOutputStream sequenceStream;
    private Logger log = Logger.getLogger(getClass().getSimpleName());

    public Automa(AutomaState startState) {
        this.currentState = startState;
        try {
            sequenceStream = new FileOutputStream(File.createTempFile("AutomaSequenceDiagram", ".txt"));
            sequenceStream.write("@startuml\n".getBytes());
        } catch (IOException e) {
            Logger.getAnonymousLogger().warning("Unable to create the .puml file");
        }
    }

    public AutomaEvent getLastEvent() {
        return lastEvent;
    }

    public void signalEvent(AutomaEvent event) {
        Comparable<AutomaEvent> comparable;
        StateAction stateAction = currentState.getStateAction(event);

        if (stateAction != null) {
            comparable = stateAction.getComparable();
            if (comparable != null) {
                if (comparable.compareTo(event) != 0) {
                    return;
                }
            }
        } else {
            log.warning("Discard event " + event.toString() + " from state " + currentState.toString());
        }

        lastEvent = event;
        if (stateAction != null) {
            Runnable action = stateAction.getAction();
            AutomaState nextState = stateAction.getStatus();
            if (sequenceStream != null) {
                String str = currentState.toString() + " -> " + nextState.toString() + ": " + event.toString() + "\n";
                try {
                    sequenceStream.write(str.getBytes());
                } catch (IOException e) {
                }
            }

            if (action != null) {
                action.run();
            }
            currentState = stateAction.getStatus();
        }
    }

    public void closeAutoma() {
        if (sequenceStream != null) {
            try {
                sequenceStream.write("@enduml".getBytes());
            } catch (IOException e) {
            }
        }
    }
}
