package org.gsc.test;

import org.gsc.automa.Automa;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaFactory;
import org.gsc.automa.AutomaState;
import org.gsc.automa.config.AutomaServiceDiscovery;
import org.gsc.test.utils.FakeFileService;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.gsc.automa.StateConnector.from;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 11/06/13
 * Time: 16.11
 * To change this template use File | Settings | File Templates.
 */
public class TestAutoma {
    private AutomaEvent evtOne = new AutomaEvent("evtOne");
    private AutomaEvent evtTwo = new AutomaEvent("evtTwo");
    private boolean runCheck = false;

    @Test
    public void shouldCreateAutoma() throws Exception {
        Logger.getAnonymousLogger().info("");
        AutomaFactory af = new AutomaFactory();
        AutomaState start = af.createState("start");
        AutomaState endState = af.createState("endState");


        from(start).stay().when(evtOne).andDoNothing();

        Runnable action = new Runnable() {
            @Override
            public void run() {
              runCheck = true;
            }
        };

        from(start).goTo(endState).when(evtTwo).andDo(action);

        Automa automa = new Automa(start);

        automa.signalEvent(evtOne);
        assertFalse(runCheck);

        automa.signalEvent(evtTwo);
        assertTrue(runCheck);
        automa.closeAutoma();
    }

    @Test
    public void shouldVerifyErrorOnSequenceDiagram() throws Exception {
        Logger.getAnonymousLogger().info("");
        FakeFileService service = new FakeFileService();
        service.setThrowExceptionOnWrite();
        AutomaServiceDiscovery.setOutputStreamService(service);

        AutomaFactory af = new AutomaFactory();
        AutomaState start = af.createState("start");

        from(start).stay().when(evtOne).andDoNothing();

        Automa automa = new Automa(start);


        automa.signalEvent(evtOne);
    }

}
