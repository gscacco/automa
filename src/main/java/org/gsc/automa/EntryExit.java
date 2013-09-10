package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: G.Scacco
 * Date: 10/09/13
 * Time: 14.46
 */
public class EntryExit<STATE extends Enum> {
    public Type getType() {
        return type;
    }

    public enum Type {
        enter, exit
    }

    private STATE state;
    private Type type;
    private Runnable action;

    public EntryExit(STATE state, Type type) {
        this.state = state;
        this.type = type;
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
