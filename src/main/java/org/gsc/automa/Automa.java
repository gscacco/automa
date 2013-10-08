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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class Automa<STATE extends Enum, EVENT extends Enum> {

    static public interface Action {
        public void run(Object payload);
    }

    private STATE initialState;
    protected STATE currentState;
    private AutomaState[] states;
    private Object payload;
    private StateActionMap<STATE> entryActions = new StateActionMap();
    private StateActionMap<STATE> exitActions = new StateActionMap();
    private boolean alreadyRunning = false;
    private Queue<EventPayload> jobs = new LinkedList<EventPayload>();
    private Map<STATE, Automa> childrenAutoma = new HashMap<STATE, Automa>();
    private HoldingStrategy strategy = HoldingStrategy.HOLD;

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa
     */
    public Automa(STATE startState) {
        this.initialState = startState;
        this.currentState = startState;
        STATE[] enumStates = (STATE[]) startState.getClass().getEnumConstants();
        int numOfStates = enumStates.length;
        states = new AutomaState[numOfStates];
        for (int i = 0; i < numOfStates; i++) {
            states[i] = new AutomaState(enumStates[i]);
        }
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
    private void handleEvent(EVENT event, Object payload) {
        this.payload = payload;
        Transition<STATE> transition = states[currentState.ordinal()].getTransition(event);
        if (transition != null && transition.getValidator().validate(payload)) {
            transit(transition, event, payload);
            applyHoldingStrategy(transition);
        } else {
            signalChildAutoma(event, payload);
        }
    }

    /**
     * Check whether the given transition requires the child automa
     * to be reset, according to the holding strategy.
     *
     * @param transition The transition to apply the holding
     *                   strategy to.
     */
    private void applyHoldingStrategy(Transition transition) {
        Automa childAutoma = childrenAutoma.get(currentState);
        if (childAutoma != null && strategy == HoldingStrategy.RESET && !transition.isLace()) {
            childAutoma.reset();
        }
    }

    /**
     * Reset the automa to its initial start state.
     */
    private void reset() {
        this.currentState = initialState;
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
     * Transit from the current state along the given transition and
     * execute the action associated with it.
     *
     * @param transition The transition to transit through.
     * @param event      The event which has triggered the transition.
     */
    protected void transit(Transition<STATE> transition, EVENT event, Object payload) {
        executeAction(transition, payload);
        currentState = transition.getEndState();
        if ( ! transition.isLace()) {
            exitActions.runAction(transition.getStartState());
            entryActions.runAction(currentState);
        }
    }

    /**
     * Execute the action associated with a given transition.
     * 
     * @param transition The transition to execute the related actions.
     */
    protected void executeAction(Transition transition, Object payload) {
      transition.getAction().run(payload);
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
     * @param strategy
     * @param childAutoma The child automa.
     */
    public void addChildAutoma(STATE state, HoldingStrategy strategy, Automa childAutoma) {
        this.strategy = strategy;
        childrenAutoma.put(state, childAutoma);
    }

    /**
     * Add a child automa which operates when this automa is in a given state.
     *
     * @param state       The state under which the child automa will operate.
     * @param childAutoma The child automa.
     */
    public void addChildAutoma(STATE state, Automa childAutoma) {
        addChildAutoma(state, HoldingStrategy.HOLD, childAutoma);
    }
}
