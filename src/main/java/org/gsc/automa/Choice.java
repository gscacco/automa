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

    public Choice(STATE state, Automa.Action action) {
    }

    public Choice(STATE state, Runnable action) {
    }

    public Choice(Automa.Action action) {
    }

    public static Choice doNothingAndStay() {
        return new Choice(nullAction);
    }
}
