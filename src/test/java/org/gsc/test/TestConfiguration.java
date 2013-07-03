package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaState;
import org.gsc.automa.config.AutomaConfiguration;
import org.gsc.automa.config.AutomaServiceDiscovery;
import org.gsc.test.utils.FakeAutomaExecutorService;
import org.junit.Test;

import static org.gsc.automa.StateConnector.from;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
     * quando .setThreading(false) l'automa eseguirà le azioni in modo sincrono
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

    @Test
    public void shouldTestConfigurationThreadingPool() {
        int num = 4;
        AutomaConfiguration.setThreading(true);
        AutomaConfiguration.setThreadsNumber(num);

        Runnable myAction = new Runnable() {
            @Override
            public void run() {
                actionExecuted = true;
            }
        };

        FakeAutomaExecutorService executorService = new FakeAutomaExecutorService();
        AutomaServiceDiscovery.setExecutorService(executorService);

        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");
        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);
        automa.signalEvent(evt);

        assertEquals(num, executorService.getThreadsNumber());
        assertEquals(1, executorService.getSubmittedJobs());
    }
}
