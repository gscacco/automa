package org.gsc.automa;

public class Transition<STATE extends Enum> {
    private final STATE startState;
    private final STATE endState;
    private final Runnable action;
    private EventValidator validator;

    public Transition(STATE startState,
                      STATE endState,
                      Runnable action,
                      EventValidator validator) {
        this.startState = startState;
        this.endState = endState;
        this.action = action;
        this.validator = validator;
    }

    public Runnable getAction() {
        return action;
    }

    public STATE getEndState() {
        return endState;
    }

    public EventValidator getValidator() {
        return validator;
    }

    public boolean isLace() {
      return startState == endState;
    }
}
