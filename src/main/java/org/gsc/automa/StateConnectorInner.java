package org.gsc.automa;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 05/04/13
 * Time: 16.54
 * To change this template use File | Settings | File Templates.
 */
public class StateConnectorInner {
    private AutomaState startState;
    private AutomaState nextState;
    private ArrayList<AutomaEvent> events;
    private Runnable action = null;
//  private Comparable<AutomaEvent> comparable;

    private static class NullAction implements Runnable {
        @Override public void run() { /* Do nothing */ }
    }

    static private NullAction nullAction = new NullAction();

    private void reset() {
        startState = null;
        nextState = null;
        events = new ArrayList<AutomaEvent>();
        action = null;
//      comparable = null;
    }

    public StateConnectorInner from(AutomaState state) {
        reset();
        this.startState = state;
        return this;
    }

    public StateConnectorInner goTo(AutomaState state) {
        this.nextState = state;
        return this;
    }

    public StateConnectorInner when(AutomaEvent event) {
        this.events.add(event);
        return this;
    }

    public void andDo(Runnable action) {
        this.action = action;
//      if (events == null) {
        for (AutomaEvent event: this.events) {
                startState.transitionTo(nextState, event, /*comparable,*/ action);
        }
//      } else {
//          startState.transitionTo(nextState, events, action);
//      }
        reset();
    }

    public void andDoNothing() {
        andDo(nullAction);
    }

    public StateConnectorInner stay() {
        nextState = startState;
        return this;
    }

    public StateConnectorInner forEach(AutomaEvent[] events) {
        for (AutomaEvent event: events) {
            this.events.add(event);
        }
        return this;
    }

//  public StateConnectorInner onlyIf(Comparable<AutomaEvent> comparable) {
//      this.comparable = comparable;
//      return this;
//  }
}
