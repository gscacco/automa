package org.gsc.automa;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.43
 * To change this template use File | Settings | File Templates.
 */
public class Automa<STATE extends Enum, EVENT extends Enum> {
    private EVENT lastEvent;
    private STATE currentState;
    private AutomaState[] states;
    private Object payload;
    private StateActionMap<STATE> entryActions;
    private StateActionMap<STATE> exitActions;
    private boolean alreadyRunning = false;
    private Queue<EventPayload> jobs = new LinkedList<EventPayload>();
    private Map<STATE, Automa> childrenAutoma = new HashMap<STATE, Automa>();

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa
     */
    public Automa(STATE startState) {
        this.currentState = startState;
        STATE[] enumStates = (STATE[]) startState.getClass().getEnumConstants();
        int numOfStates = enumStates.length;
        states = new AutomaState[numOfStates];
        for (int i = 0; i < numOfStates; i++) {
            states[i] = new AutomaState(enumStates[i]);
        }
        entryActions = new StateActionMap();
        exitActions = new StateActionMap();
    }

    /**
     * This method is normaly used in the action. It returns the last event signaled to the automa
     *
     * @return The last event
     */
    public EVENT getLastEvent() {
        return lastEvent;
    }

    public StateConnector<STATE, EVENT> from(STATE state) {
        return new StateConnector<STATE, EVENT>(states[state.ordinal()]);
    }

    /**
     * Handle an automa event by executing the action associated
     * with the state transition and then transit the automa to the
     * related new state. However if the validation of the event
     * object fails, the transition won't take place.
     *
     * @param event   The event to handle.
     * @param payload An optional payload associated with the signal.
     */
    protected void handleEvent(EVENT event, Object payload) {
        this.payload = payload;
        Transition<STATE> transition = states[currentState.ordinal()].getTransition(event);
        if (transition != null && transition.getValidator().validate(payload)) {
            Runnable action = transition.getAction();
            transit(currentState, transition.getEndState(), action, event);
        } else {
            signalChildAutoma(event, payload);
        }
    }

    /**
     * Signal an event to the child automa.
     *
     * @param event   The event to signal to the child automa.
     * @param payload An optional payload associated with the signal.
     */
    private void signalChildAutoma(EVENT event, Object payload) {
        Automa childAutoma = childrenAutoma.get(currentState);
        if (childAutoma != null) {
            childAutoma.signalEvent(event, payload);
        }
    }

    /**
     * Transit from a state to its following one and execute the
     * action associated with the transition.
     *
     * @param startState The state the transition starts from.
     * @param endState   The state the transition ends to.
     * @param action     The action to be executed along this transition.
     * @param event      The event which has triggered the transition.
     */
    protected void transit(STATE startState, STATE endState,
                           Runnable action, EVENT event) {
        lastEvent = event;
        action.run();
        currentState = endState;
        if (endState != startState) {
            exitActions.runAction(startState);
            entryActions.runAction(endState);
        }
    }

    /**
     * This method is used to signal an event to the automa
     *
     * @param event The event
     */
    public synchronized void signalEvent(EVENT event) {
        signalEvent(event, new Object());
    }

    /**
     * Signal an event to the automa.
     *
     * @param event   The event to signal.
     * @param payload A payload to associate with the event.
     */
    public synchronized void signalEvent(EVENT event, Object payload) {
        if (alreadyRunning) {
            jobs.add(new EventPayload(event, payload));
        } else {
            alreadyRunning = true;
            handleEvent(event, payload);
            while (!jobs.isEmpty()) {
                EventPayload job = jobs.poll();
                handleEvent(job.event, job.payload);
            }
            alreadyRunning = false;
        }

    }

    public Object getPayload() {
        return payload;
    }

    /**
     * Set the action to be executed when entering a state.
     *
     * @param state  The state.
     * @param action The action to be executed.
     */
    public void onceIn(STATE state, Runnable action) {
        entryActions.put(state, action);
    }

    /**
     * Set the action to be executed when leaving from a state.
     *
     * @param state  The state.
     * @param action The action to be executed.
     */
    public void onceOut(STATE state, Runnable action) {
        exitActions.put(state, action);
    }

    private class EventPayload {
        EVENT event;
        Object payload;

        public EventPayload(EVENT event, Object payload) {
            this.event = event;
            this.payload = payload;
        }
    }

    /**
     * Add a child automa which operates when this automa is in a given state.
     *
     * @param state       The state under which the child automa will operate.
     * @param childAutoma The child automa.
     */
    public void addChildAutoma(STATE state, Automa childAutoma) {
        childrenAutoma.put(state, childAutoma);
    }
}
