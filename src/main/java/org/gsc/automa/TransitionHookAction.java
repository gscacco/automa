package org.gsc.automa;

/**
 * Created by gianluca on 18/02/14.
 */
abstract public class TransitionHookAction {
    private Enum fromState;
    private Enum toState;

    protected TransitionHookAction(Enum fromState, Enum toState) {
        this.fromState = fromState;
        this.toState = toState;
    }

    public TransitionHookAction() {

    }

    abstract public void run(Enum fromState, Enum toState);
}
