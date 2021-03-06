/*
 * Copyright 2013 Gianluca Scacco & Raffaele Rossi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Gianluca Scacco <gianluca.scacco@gmail.com>
 * Raffaele Rossi <rossi.raffaele@gmail.com>
 */
package org.gsc.test.utils;

import org.junit.Assert;

public class SpyAction implements Runnable {

    private int numOfExecution = 0;

    @Override
    public void run() {
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

}
