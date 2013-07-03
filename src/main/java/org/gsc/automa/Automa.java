package org.gsc.automa;

import org.gsc.automa.config.AutomaConfiguration;
import org.gsc.automa.config.AutomaExecutorService;
import org.gsc.automa.config.AutomaServiceDiscovery;
import org.gsc.automa.config.IAutomaExecutorService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private BlockingQueue<AutomaEvent> automaEvents = new LinkedBlockingQueue<AutomaEvent>();

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa
     */
    public Automa(AutomaState startState) {
        if (AutomaServiceDiscovery.getExecutorService() == null) {
            AutomaExecutorService executorService = new AutomaExecutorService();
            AutomaServiceDiscovery.setExecutorService(executorService);
        }
        this.currentState = startState;
        try {
            sequenceStream = new FileOutputStream(File.createTempFile("AutomaSequenceDiagram", ".txt"));
            sequenceStream.write("@startuml\n".getBytes());
        } catch (IOException e) {
            Logger.getAnonymousLogger().warning("Unable to create the .txt file");
        }

        if (AutomaConfiguration.isThreading()) {
            Logger.getAnonymousLogger().info("Automa started in asynchronous mode");
        } else {
            Logger.getAnonymousLogger().info("Automa started in synchronous mode");
        }
    }

    /**
     * This method is normaly used in the action. It returns the last event signaled to the automa
     *
     * @return The last event
     */
    public AutomaEvent getLastEvent() {
        return lastEvent;
    }

    private void handleEvent(AutomaEvent event) {
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

    /**
     * This method is used to signal an event to the automa
     *
     * @param event The event
     */
    public void signalEvent(final AutomaEvent event) throws Exception {
        if (AutomaConfiguration.isThreading()) {
            IAutomaExecutorService executorService = AutomaServiceDiscovery.getExecutorService();
            Runnable job = new Runnable() {
                @Override
                public void run() {
                    handleEvent(event);
                }
            };
            if (executorService != null) {
                executorService.submitJob(job);
            } else {
                throw new Exception("The automa is multithread but no executor service available");
            }

        } else {
            handleEvent(event);
        }
    }

    /**
     * This method should be called at the end of the execution of the automa.
     * It also writes @enduml at the end of the temporary file containing the sequence diagram.
     */
    public void closeAutoma() {
        if (sequenceStream != null) {
            try {
                sequenceStream.write("@enduml".getBytes());
            } catch (IOException e) {
            }
        }
        if (AutomaServiceDiscovery.getExecutorService() != null) {
            AutomaServiceDiscovery.getExecutorService().stopService();
        }

    }
}
