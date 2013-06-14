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
    private Comparable<AutomaEvent> comparable;

    public StateAction(AutomaState state, Runnable action) {
        this.state = state;
        this.action = action;
    }

    public StateAction(AutomaState state, Runnable action, Comparable<AutomaEvent> comparable) {
        this.state = state;
        this.action = action;
        this.comparable = comparable;
    }

    public Runnable getAction() {
        return action;
    }

    public AutomaState getStatus() {
        return state;
    }

    public Comparable<AutomaEvent> getComparable() {
        return comparable;
    }
}
