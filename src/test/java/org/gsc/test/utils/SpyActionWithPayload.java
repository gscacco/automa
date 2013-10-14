package org.gsc.test.utils;

import org.gsc.automa.Automa;
import org.junit.Assert;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 14/10/13
 * Time: 17.05
 * To change this template use File | Settings | File Templates.
 */
public class SpyActionWithPayload implements Automa.Action {
    private int numOfExecution = 0;

    @Override
    public void run(Object payload) {
        numOfExecution++;
    }

    public void assertExecuted() {
        Assert.assertTrue("Action not executed", numOfExecution > 0);
    }
}
