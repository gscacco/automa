package org.gsc.automa;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.44
 * To change this template use File | Settings | File Templates.
 */
public class AutomaState<STATE extends Enum, EVENT extends Enum> {

    private STATE state;
    private Transition[] transitions;

    public AutomaState(STATE state, Class<EVENT> eventClass) {
        this.state = state;
        this.transitions = new Transition[eventClass.getEnumConstants().length];
    }

    public STATE getState() {
      return state;
    }

    public Transition getTransition(EVENT event) {
        Transition t = transitions[event.ordinal()];
        if (t == null) {
            throw new RuntimeException(String.format("Unmapped event %s from state %s", event, state));
        }
        return t;
    }

    public void transitTo(STATE endState, EVENT event, EventValidator validator, Runnable action) {
        transitions[event.ordinal()] = new Transition(endState, action, validator);
    }

}
