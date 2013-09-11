package org.gsc.test.utils;

import org.junit.Assert;

public class SpyAction implements Runnable {

    private int numOfExecution = 0;

    @Override
    public void run() { numOfExecution++; }

    public void assertNotExecuted() {
        Assert.assertTrue("Action executed", numOfExecution == 0);
    }

    public void assertExecuted() {
        Assert.assertTrue("Action not executed", numOfExecution > 0);
    }

    public void assertExecuted(int expectedNumber) {
        Assert.assertEquals("Number of executions", expectedNumber, numOfExecution);
    }

}
