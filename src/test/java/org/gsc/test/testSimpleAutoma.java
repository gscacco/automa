package org.gsc.test;

import org.gsc.automa.*;
import org.junit.Assert;
import org.junit.Test;

import static org.gsc.automa.StateConnector.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 11/06/13
 * Time: 16.11
 * To change this template use File | Settings | File Templates.
 */
public class testSimpleAutoma {
    private AutomaEvent evtOne = new AutomaEvent("evtOne");
    private AutomaEvent evtTwo = new AutomaEvent("evtTwo");
    private boolean runCheck = false;
    private Boolean syncVar = false;

    @Test
    public void shouldCreateAutoma() {

        AutomaFactory af = new AutomaFactory();
        AutomaState start = af.createState("start");
        AutomaState endState = af.createState("endState");


        from(start).stay().when(evtOne).andDoNothing();


        Runnable action = new Runnable() {
            @Override
            public void run() {
                synchronized (syncVar) {
                    runCheck = true;
                    syncVar.notifyAll();
                }
            }
        };

        from(start).goTo(endState).when(evtTwo).andDo(action);

        Automa automa = new Automa(start);

        automa.signalEvent(evtOne);
        assertFalse(runCheck);

        automa.signalEvent(evtTwo);
        synchronized (syncVar) {
            try {
                syncVar.wait();
            } catch (InterruptedException e) {
            }
        }
        assertTrue(runCheck);

        automa.closeAutoma();

    }

}
