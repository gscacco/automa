package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 14/10/13
 * Time: 16.50
 * To change this template use File | Settings | File Templates.
 */
public class RunnableActionAdapter implements Automa.Action {
    private Runnable runnable;

    RunnableActionAdapter(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run(Object obj) {
        runnable.run();
    }
}
