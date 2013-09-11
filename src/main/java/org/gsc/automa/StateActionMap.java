package org.gsc.automa;

import java.util.HashMap;

public class StateActionMap<STATE extends Enum> extends HashMap<Integer, Runnable> {

    public void put(STATE s, Runnable r) { put(s.ordinal(), r); }

    public void runAction(STATE s) {
        Runnable r = get(s.ordinal());
        if (r != null) {
            r.run();
        }
    }

}
