package org.gsc.automa;

/**
 * Created by gianluca on 18/02/14.
 */
abstract public class TransitionHookAction {
    abstract public void run(Enum fromState, Enum toState);
}
