package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaState;
import org.gsc.automa.config.AutomaConfiguration;
import org.gsc.automa.config.AutomaServiceDiscovery;
import org.gsc.test.utils.FakeAutomaExecutorService;
import org.junit.Before;
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

    Runnable myAction = new Runnable() {
        @Override
        public void run() {
            actionExecuted = true;
        }
    };

    @Before
    public void before() {
        actionExecuted = false;
        AutomaConfiguration.setThreading(true);
        AutomaConfiguration.setThreadsNumber(1);
        AutomaServiceDiscovery.setExecutorService(null);
    }

    /**
     * Questo test verifica la corretta configurazione dell'automa
     * quando .setThreading(false) l'automa eseguirà le azioni in modo sincrono
     */
    @Test
    public void shouldTestConfiguration() throws Exception {
        AutomaConfiguration.setThreading(false);
        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");

        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);

        automa.signalEvent(evt);
        assertTrue(actionExecuted);
        automa.closeAutoma();
    }

    @Test
    public void shouldTestConfigurationThreadingPool() throws Exception {
        int num = 4;
        AutomaConfiguration.setThreading(true);
        AutomaConfiguration.setThreadsNumber(num);

        FakeAutomaExecutorService executorService = new FakeAutomaExecutorService();
        AutomaServiceDiscovery.setExecutorService(executorService);

        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");
        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);
        automa.signalEvent(evt);

        assertEquals(num, executorService.getThreadsNumber());
        assertEquals(1, executorService.getSubmittedJobs());
        automa.closeAutoma();
    }

    @Test
    public void shouldVerifyNoThreads() throws Exception {
        AutomaConfiguration.setThreading(false);

        FakeAutomaExecutorService executorService = new FakeAutomaExecutorService();
        AutomaServiceDiscovery.setExecutorService(executorService);

        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");
        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);
        automa.signalEvent(evt);
        assertEquals(0, executorService.getSubmittedJobs());
        automa.closeAutoma();
    }

    @Test(expected = Exception.class)
    public void shouldVerifyThreadButNoExecutorService() throws Exception {
        int num = 4;
        AutomaConfiguration.setThreading(true);
        AutomaConfiguration.setThreadsNumber(num);


        AutomaState startState = new AutomaState("StartState");
        AutomaEvent evt = new AutomaEvent("Evt");
        from(startState).stay().when(evt).andDo(myAction);

        Automa automa = new Automa(startState);

        //Set the executor service to null after the instantiation of the automa
        AutomaServiceDiscovery.setExecutorService(null);

        automa.signalEvent(evt);
        automa.closeAutoma();
    }
}
