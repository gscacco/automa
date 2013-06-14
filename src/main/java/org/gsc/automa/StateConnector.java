package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 05/04/13
 * Time: 16.15
 * To change this template use File | Settings | File Templates.
 */
public class StateConnector {
    private static final StateConnectorInner stateConnectorInner = new StateConnectorInner();

    public static StateConnectorInner from(AutomaState state) {
        return stateConnectorInner.from(state);
    }

    public static StateConnectorInner when(AutomaEvent event) {
        return stateConnectorInner.when(event);
    }
}
