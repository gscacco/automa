package org.gsc.automa;

import java.util.concurrent.ExecutorService;

/**
 * Extends Automa to provide an asynchronous automa, that is an 
 * automa capable of executing actions on a seprate thread. 
 * 
 * @author raffaelerossi (7/24/2013)
 */
public class AsyncAutoma extends Automa {

    /** 
     * The executor service to submit action jobs to. 
     */
    private ExecutorService execService;

    /**
     * Construct an AsyncAutoma.
     * 
     * @author raffaelerossi (7/24/2013)
     * 
     * @param execService The executor service to submit action jobs to.
     * @param startState  The automa's start state.
     */
    public AsyncAutoma(ExecutorService execService, AutomaState startState) {
        super(startState);
        this.execService = execService;
    }

    /**
     * Overrides the handleEvent to just delegate the parent 
     * implementation on a separate thread. 
     */
    @Override
    protected void handleEvent(final AutomaEvent event) {
        this.execService.submit(new Runnable() {
            @Override
            public void run() {
                doHandleEvent(event);
            }
        });
    }

    private void doHandleEvent(AutomaEvent event) {
      super.handleEvent(event);
    }

    @Override
    public void closeAutoma() {
        super.closeAutoma();
    }
}
