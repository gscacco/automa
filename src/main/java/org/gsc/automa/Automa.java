package org.gsc.automa;

import java.util.ArrayList;
import java.util.List;

import static org.gsc.automa.EntryExit.Type;

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
    private List<EntryExit> entryExitList = new ArrayList<EntryExit>();
    private Object payload;

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
            executeInOrOutAction(currentState, Type.enter);
            executeInOrOutAction(startState, Type.exit);
        }
    }

    /**
     * Executes the entry action if present
     */
    private void executeInOrOutAction(STATE state, Type type) {
        Runnable action;
        for (EntryExit entryExit : entryExitList) {
            boolean sameState = state.ordinal() == entryExit.getState().ordinal();
            boolean enterType = entryExit.getType() == type;

            if (sameState && enterType) {
                action = entryExit.getAction();
                if (action != null) {
                    action.run();
                }
            }
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
        handleEvent(event, payload);
    }

    public Object getPayload() {
        return payload;
    }


    /**
     * Entry action method
     *
     * @param state The state
     * @return
     */
    public EntryExit<STATE> onceIn(STATE state) {
        EntryExit entryExit = new EntryExit(state, Type.enter);
        entryExitList.add(entryExit);
        return entryExit;
    }

    public EntryExit<STATE> onceOut(STATE state) {
        EntryExit entryExit = new EntryExit(state, Type.exit);
        entryExitList.add(entryExit);
        return entryExit;
    }
}
