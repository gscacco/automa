/*
 * Copyright 2013 Gianluca Scacco
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
 */
package org.gsc.test;

import org.gsc.automa.AsyncAutoma;

import org.gsc.test.utils.AutomaTestCase;
import org.gsc.test.utils.AutomaTestCase.FakeEvent;
import org.gsc.test.utils.AutomaTestCase.FakeState;
import org.junit.Before;
import org.junit.Test;


public class TestAsyncAutoma extends AutomaTestCase {

  /**
   * Maximum one second to wait an asynchronous action to be executed. 
   */
  static private final long WAIT_TIMEOUT = 1000;

  class SpyAction implements Runnable {
    long tid;
    @Override
    synchronized public void run() {
      tid = Thread.currentThread().getId();
      notify();
    }

    synchronized public void assertExecutedOnDifferentThread(Thread thread)
    throws Exception {
      wait(WAIT_TIMEOUT);
      assertTrue("Action not executed", tid != 0);
      assertTrue("Action executed on same thread",
                 tid != thread.getId());
    }
  }

  @Test
  public void shouldRunActionOnSeparateThread() throws Exception {
    // setup
    SpyAction spyAction = new SpyAction();
    AsyncAutoma automa = new AsyncAutoma(FakeState.STATE_1);
    automa.from(FakeState.STATE_1).stay().when(FakeEvent.EVENT_1).andDo(spyAction);
    // exercise
    automa.signalEvent(FakeEvent.EVENT_1);
    // verify
    spyAction.assertExecutedOnDifferentThread(Thread.currentThread());
  }

}
