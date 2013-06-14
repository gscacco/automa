package org.gsc.automa;

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
    private AutomaEvent event;
    private Runnable action = null;
    private AutomaEvent[] events;
    private Comparable<AutomaEvent> comparable;

    private void reset() {
        startState = null;
        nextState = null;
        event = null;
        action = null;
        events = null;
        comparable = null;
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
        this.event = event;
        return this;
    }

    public void andDo(Runnable action) {
        this.action = action;
        if (events == null) {
            startState.transitionTo(nextState, event, comparable, action);
        } else {
            startState.transitionTo(nextState, events, action);
        }
        reset();
    }

    public void andDoNothing() {
        this.action = null;
        if (events == null) {
            startState.transitionTo(nextState, event, comparable, action);
        } else {
            startState.transitionTo(nextState, events, action);
        }
        reset();
    }

    public StateConnectorInner stay() {
        nextState = startState;
        return this;
    }

    public StateConnectorInner forEach(AutomaEvent[] events) {
        this.events = events;
        return this;
    }

    public StateConnectorInner onlyIf(Comparable<AutomaEvent> comparable) {
        this.comparable = comparable;
        return this;
    }
}
