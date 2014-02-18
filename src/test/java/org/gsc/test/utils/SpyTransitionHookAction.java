package org.gsc.test.utils;

import org.gsc.automa.TransitionHookAction;
import org.junit.Assert;

/**
 * Created by gianluca on 18/02/14.
 */
public class SpyTransitionHookAction extends TransitionHookAction {
    private int numOfExecution = 0;
    private Enum oldStatus;
    private Enum toStatus;

    @Override
    public void run(Enum fromStatus, Enum toStatus) {
        oldStatus = fromStatus;
        this.toStatus = toStatus;
        numOfExecution++;
    }

    public void assertNotExecuted() {
        Assert.assertTrue("Action executed", numOfExecution == 0);
    }

    public void assertExecuted() {
        Assert.assertTrue("Action not executed", numOfExecution > 0);
    }

    public void assertExecuted(int expectedNumber) {
        Assert.assertEquals("Number of executions", expectedNumber, numOfExecution);
    }

    public Enum getFromStatus() {
        return oldStatus;
    }

    public Enum getToStatus() {
        return toStatus;
    }
}
