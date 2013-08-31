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
    private HashMap<Integer, Transition<STATE>> transitions = new HashMap<Integer, Transition<STATE>>();

    public AutomaState(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

    public Transition getTransition(EVENT event) {
        return transitions.get(event.ordinal());
    }

    public void transitTo(STATE endState, EVENT event, EventValidator validator, Runnable action) {
        transitions.put(event.ordinal(), new Transition(endState, action, validator));
    }

}
