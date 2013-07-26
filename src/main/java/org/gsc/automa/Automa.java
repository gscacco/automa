package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.43
 * To change this template use File | Settings | File Templates.
 */
public class Automa {
    private AutomaEvent lastEvent;
    private AutomaState currentState;

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa
     */
    public Automa(AutomaState startState) {
        this.currentState = startState;
    }

    /**
     * This method is normaly used in the action. It returns the last event signaled to the automa
     *
     * @return The last event
     */
    public AutomaEvent getLastEvent() {
        return lastEvent;
    }

    /**
     * Handle an automa event by executing the action associated 
     * with the state transition and then transit the automa to the 
     * related new state. However if the validation of the event 
     * object fails, the transition won't take place. 
     * 
     * @param event The event to handle.
     */
    protected void handleEvent(AutomaEvent event) {
        StateAction stateAction = currentState.getStateAction(event);
        EventValidator validator = stateAction.getValidator();
        if (validator.validate(event.getCurrentObject())) {
            Runnable action = stateAction.getAction();
            transit(currentState, stateAction.getStatus(), action, event);
        }
    }

    /**
     * Transit from a state to its following one and execute the 
     * action associated with the transition.
     * 
     * @param startState The state the transition starts from.
     * @param endState The state the transition ends to.
     * @param action The action to be executed along this transition. 
     * @param event The event which has triggered the transisition. 
     */
    protected void transit(AutomaState startState, AutomaState endState,
                           Runnable action, AutomaEvent event) {
      lastEvent = event;
      action.run();
      currentState = endState;
    }

    /**
     * This method is used to signal an event to the automa
     *
     * @param event The event
     */
    public void signalEvent(AutomaEvent event) {
        handleEvent(event);
    }

}
