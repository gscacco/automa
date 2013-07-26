package org.gsc.automa;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.44
 * To change this template use File | Settings | File Templates.
 */
public class AutomaState {
    private String name;
    private HashMap<AutomaEvent, StateAction> map = new HashMap<AutomaEvent, StateAction>();

    public AutomaState(String name) {
        this.name = name;
    }

    public StateAction getStateAction(AutomaEvent event) {
        StateAction action = map.get(event);
        if (action == null) {
            throw new RuntimeException(String.format("Unmapped event %s from state %s", event, this.name));
        }
        return action;
    }

    public void transitionTo(AutomaState state, AutomaEvent event, EventValidator validator, Runnable action) {
        map.put(event, new StateAction(state, action, validator));
    }

    @Override
    public String toString() {
        return name;
    }

}
