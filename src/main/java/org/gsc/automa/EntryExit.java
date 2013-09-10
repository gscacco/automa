package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: G.Scacco
 * Date: 10/09/13
 * Time: 14.46
 */
public class EntryExit<STATE extends Enum> {
    private STATE state;
    private Runnable action;

    public EntryExit(STATE state) {
        this.state = state;
    }

    public void executeAction(Runnable action) {
        this.action = action;
    }

    public STATE getState() {
        return state;
    }

    public Runnable getAction() {
        return action;
    }
}
