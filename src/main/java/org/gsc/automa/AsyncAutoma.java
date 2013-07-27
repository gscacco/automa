package org.gsc.automa;

import java.util.concurrent.ExecutorService;

/**
 * Extends Automa to provide an asynchronous automa, that is an 
 * automa capable of executing actions on a seprate thread. 
 * 
 * @author raffaelerossi (7/24/2013)
 */
public class AsyncAutoma<STATE extends Enum, EVENT extends Enum> extends Automa<STATE, EVENT> {

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
     * @param eventClass The class of the event being signalled by this automa.
     */
    public AsyncAutoma(ExecutorService execService, STATE startState, Class<EVENT> eventClass) {
        super(startState, eventClass);
        this.execService = execService;
    }

    /**
     * Overrides the handleEvent to just delegate the parent 
     * implementation on a separate thread. 
     */
    @Override
    public void signalEvent(final EVENT event) {
        this.execService.submit(new Runnable() {
            @Override
            public void run() {
                handleEvent(event, new Object());
            }
        });
    }

}
