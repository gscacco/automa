package org.gsc.automa;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.omg.CORBA.TIMEOUT;

/**
 * Extends Automa to provide an asynchronous automa, that is an 
 * automa capable of executing actions on a seprate thread. 
 * 
 * @author raffaelerossi (7/24/2013)
 */
public class AsyncAutoma<STATE extends Enum, EVENT extends Enum> extends Automa<STATE, EVENT> {

    private class AsyncEvent<EVENT> {
        EVENT event;
        Object payload;
        AsyncEvent(EVENT event, Object payload) {
            this.event = event;
            this.payload = payload;
        }
    }

    /** 
     * The thread on which executes actions on.
     */
    private Thread _thread;

    /**
     * The queue of the signalled events to handle.
     */
    private BlockingQueue<AsyncEvent> _eventsQueue;

    /**
     * The flag to stop the thread.
     */
    private AtomicBoolean _stopped;

    /**
     * Construct an AsyncAutoma.
     * 
     * @author raffaelerossi (7/24/2013)
     * 
     * @param execService The executor service to submit action jobs to.
     * @param startState  The automa's start state.
     * @param eventClass The class of the event being signalled by this automa.
     */
    public AsyncAutoma(STATE startState, Class<EVENT> eventClass) {
        super(startState, eventClass);
        _stopped = new AtomicBoolean(false);
        _eventsQueue = new LinkedBlockingQueue<AsyncEvent>();
        _thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    while ( ! _stopped.get()) {
                        try { 
                          AsyncEvent<EVENT> asyncEvent = _eventsQueue.take(); 
                          // A null event unblocks the queue to stop the thread
                          if (asyncEvent.event != null) {
                            handleEvent(asyncEvent.event, asyncEvent.payload);
                          }
                        } catch (InterruptedException e) {
                            /* Do nothing */
                        }
                    }
                }
            });
        _thread.start();
    }

    /**
     * Overrides the the parent implementation to handle the 
     * automa's internal thread. 
     *  
     * @param event The event to signal. 
     * @param param A payload to associate with the event. 
     */
    @Override
    public void signalEvent(EVENT event, Object payload) {
        try { 
          _eventsQueue.put(new AsyncEvent(event, payload));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeAutoma() {
        _stopped.set(true);
        try { 
          _eventsQueue.put(new AsyncEvent(null, null));
          _thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
