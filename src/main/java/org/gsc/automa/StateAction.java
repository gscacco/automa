package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 17.04
 * To change this template use File | Settings | File Templates.
 */
public class StateAction {
    private final AutomaState state;
    private final Runnable action;
    private EventValidator validator;

    public StateAction(AutomaState state, Runnable action, EventValidator validator) {
        this.state = state;
        this.action = action;
        this.validator = validator;
    }

    public Runnable getAction() {
        return action;
    }

    public AutomaState getStatus() {
        return state;
    }

    public EventValidator getValidator() {
        return validator;
    }
}
