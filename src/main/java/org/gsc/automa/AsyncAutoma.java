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

package org.gsc.automa;

import java.util.concurrent.LinkedBlockingQueue;

public class AsyncAutoma<STATE extends Enum, EVENT extends Enum> extends Automa<STATE, EVENT> {
    private Thread jobsThread;
    private LinkedBlockingQueue<EventPayload> linkedBlockingJobs = (LinkedBlockingQueue<EventPayload>) jobs;

    /**
     * AsyncAutoma constructor
     *
     * @param startState The start state of the automa
     */
    public AsyncAutoma(STATE startState) {
        super(startState);
        jobsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        EventPayload eventPayLoad = linkedBlockingJobs.take();
                        handleEvent(eventPayLoad.event, eventPayLoad.payload);
                    } catch (InterruptedException e) {
                        // TODO
                    }
                }
            }
        });
        jobsThread.start();
    }

    @Override
    public synchronized void signalEvent(EVENT event, Object payload) {
        jobs.add(new EventPayload(event, payload));
    }

    @Override
    public synchronized void signalEvent(EVENT event) {
        signalEvent(event, new Object());
    }
}
