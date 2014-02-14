package org.gsc.automa;

/**
 * Created by gianluca on 12/02/14.
 */
public class Choice<STATE extends Enum> {
    private static Automa.Action nullAction = new Automa.Action() {
        @Override
        public void run(Object payload) {

        }
    };
    private STATE state;
    private Automa.Action action;


    public Choice(STATE state, Automa.Action action) {
        this.state = state;
        this.action = action;
    }

    public Choice(STATE state, Runnable action) {
        this.state = state;
        this.action = new RunnableActionAdapter(action);
    }


    public Choice(Automa.Action action) {
    }

    public static Choice doNothingAndStay() {
        return new Choice(nullAction);
    }

    public STATE getState() {
        return state;
    }

    public Automa.Action getAction() {
        return action;
    }
}
