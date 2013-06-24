package org.gsc.test;

import org.gsc.automa.*;
import org.junit.Assert;
import org.junit.Test;

import static org.gsc.automa.StateConnector.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 24/06/13
 * Time: 11.29
 * To change this template use File | Settings | File Templates.
 */
public class TestConfiguration {
    private boolean actionExecuted = false;

    /**
     * Questo test verifica la corretta configurazione dell'automa
     * quando .setThreading(false) l'automa eseguirà le azioni in modo scincrono
     */
    @Test
    public void shouldTestConfiguration() {
        AutomaConfiguration.setThreading(false);
        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");


        Runnable myAction = new Runnable() {
            @Override
            public void run() {
                actionExecuted = true;
            }
        };

        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);

        automa.signalEvent(evt);
        assertTrue(actionExecuted);
    }
}
