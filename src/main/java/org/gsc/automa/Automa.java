package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.43
 * To change this template use File | Settings | File Templates.
 */
public class Automa<STATE extends Enum, EVENT extends Enum> {
    private EVENT lastEvent;
    private STATE currentState;
    private AutomaState[] states;

    /**
     * Automa constructor
     *
     * @param startState The start state of the automa 
     * @param eventClass The class of the event being signalled by this automa.
     */
    public Automa(STATE startState, Class<EVENT> eventClass) {
        this.currentState = startState;
        STATE[] enumStates = (STATE[])startState.getClass().getEnumConstants();
        int numOfStates = enumStates.length;
        states = new AutomaState[numOfStates];
        for (int i=0; i < numOfStates; i++) {
            states[i] = new AutomaState(enumStates[i], eventClass);
        }
    }

    /**
     * This method is normaly used in the action. It returns the last event signaled to the automa
     *
     * @return The last event
     */
    public EVENT getLastEvent() {
        return lastEvent;
    }

    public StateConnector<STATE, EVENT> from(STATE startState) {
        return new StateConnector<STATE, EVENT>(states[startState.ordinal()]);
    }

    /**
     * Handle an automa event by executing the action associated 
     * with the state transition and then transit the automa to the 
     * related new state. However if the validation of the event 
     * object fails, the transition won't take place. 
     * 
     * @param event The event to handle. 
     * @param payload An optional payload associated with the signal.
     */
    protected void handleEvent(EVENT event, Object payload) {
        Transition<STATE> transition = states[currentState.ordinal()].getTransition(event);
        EventValidator validator = transition.getValidator();
        if (validator.validate(payload)) {
            Runnable action = transition.getAction();
            transit(currentState, transition.getEndState(), action, event);
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
    protected void transit(STATE startState, STATE endState,
                           Runnable action, EVENT event) {
      lastEvent = event;
      action.run();
      currentState = endState;
    }

    /**
     * This method is used to signal an event to the automa
     *
     * @param event The event
     */
    public void signalEvent(EVENT event) {
        handleEvent(event, new Object());
    }

}
