package org.gsc.automa;

import org.gsc.automa.config.*;

import java.io.IOException;
import java.util.logging.Level;
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
    private IOutputStreamService sequenceStream;
    private Logger log = Logger.getLogger(getClass().getSimpleName());
    private boolean firstSignal = true;

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa
     */
    public Automa(AutomaState startState) {
        this.currentState = startState;
        sequenceStream = AutomaServiceDiscovery.getOutputStreamService();
    }

    /**
     * This method is normaly used in the action. It returns the last event signaled to the automa
     *
     * @return The last event
     */
    public AutomaEvent getLastEvent() {
        return lastEvent;
    }

    protected void handleEvent(AutomaEvent event) {
        StateAction stateAction = currentState.getStateAction(event);
        EventValidator validator = stateAction.getValidator();
        if (validator.validate(event.getCurrentObject())) {
            lastEvent = event;
            Runnable action = stateAction.getAction();
            AutomaState nextState = stateAction.getStatus();
            if (sequenceStream != null) {
                String str = currentState.toString() + " -> " + nextState.toString() + ": " + event.toString() + "\n";
                try {
                    sequenceStream.write(str);
                } catch (IOException e) {
                }
            }
            action.run();
            currentState = stateAction.getStatus();
        }
    }

    /**
     * This method is used to signal an event to the automa
     *
     * @param event The event
     */
    public void signalEvent(final AutomaEvent event) {
        if (this.firstSignal) {
            setupAutomaConfig();
            this.firstSignal = false;
        }
        handleEvent(event);
    }

    private void setupAutomaConfig() {
        if (AutomaServiceDiscovery.getOutputStreamService() == null) {
            AutomaServiceDiscovery.setOutputStreamService(new FileOutputStreamService());
            try {
                AutomaServiceDiscovery.getOutputStreamService().write("@startuml\n");
            } catch (IOException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "File not available");
            }
        }
        sequenceStream = AutomaServiceDiscovery.getOutputStreamService();
    }

    /**
     * This method should be called at the end of the execution of the automa.
     * It also writes @enduml at the end of the temporary file containing the sequence diagram.
     */
    public void closeAutoma() {
        Logger.getAnonymousLogger().info("Closing automa on state " + currentState);
        if (sequenceStream != null) {
            try {
                sequenceStream.write("@enduml");
            } catch (IOException e) {
            }
        }
    }
}
