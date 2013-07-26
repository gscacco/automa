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

    public void transitionTo(AutomaState state, AutomaEvent event, Runnable action) {
        map.put(event, new StateAction(state, action));
    }

//  public void transitionTo(AutomaState state, AutomaEvent events[], Runnable action) {
//      for (AutomaEvent event : events) {
//          transitionTo(state, event, action);
//      }
//  }

    public void stay(AutomaEvent event, Runnable action) {
        map.put(event, new StateAction(this, action));
    }

    @Override
    public String toString() {
        return name;
    }

    public StateAction getStateAction(AutomaEvent event) {
        StateAction action = map.get(event);
        if (action == null) {
            throw new RuntimeException(String.format("Unmapped event %s from state %s", event, this.name));
        }
        return action;
    }

//  public void stay(AutomaEvent[] events, Runnable action) {
//      for (AutomaEvent event : events) {
//          stay(event, action);
//      }
//  }

//  public void transitionTo(AutomaState state, AutomaEvent event, Comparable<AutomaEvent> comparable, Runnable action) {
//      map.put(event, new StateAction(state, action, comparable));
//  }
}
