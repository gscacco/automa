package org.gsc.automa;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 05/04/13
 * Time: 16.54
 * To change this template use File | Settings | File Templates.
 */
public class StateConnector<STATE extends Enum, EVENT extends Enum> {
    private AutomaState<STATE, EVENT> startState;
    private STATE nextState;
    private ArrayList<EVENT> events;
    private Runnable action = null;
    private EventValidator validator;

    static private Runnable nullAction = new Runnable() {
        @Override public void run() { /* Do nothing */ }
    };

    static private EventValidator nullValidator = new EventValidator() {
        @Override public boolean validate(Object obj) { return true; }
    };

    public StateConnector(AutomaState<STATE, EVENT> startState) {
        this.startState = startState;
        this.nextState = startState.getState();
        this.events = new ArrayList<EVENT>();
        this.action = nullAction;
        this.validator = nullValidator;
    }

    public StateConnector goTo(STATE state) {
        this.nextState = state;
        return this;
    }

    public StateConnector when(EVENT event) {
        this.events.add(event);
        return this;
    }

    public void andDo(Runnable action) {
        this.action = action;
        for (EVENT event: this.events) {
                startState.transitTo(nextState, event, validator, action);
        }
    }

    public void andDoNothing() {
        andDo(this.action);
    }

    public StateConnector stay() {
        nextState = startState.getState();
        return this;
    }

    public StateConnector forEach(EVENT... events) {
        for (EVENT event: events) {
            this.events.add(event);
        }
        return this;
    }

    public StateConnector onlyIf(EventValidator validator) {
        this.validator = validator;
        return this;
    }
}
