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

package org.gsc.automa;

public class AsyncAutoma<STATE extends Enum, EVENT extends Enum> 
extends Automa<STATE, EVENT> {

    /**
     * AsyncAutoma constructor
     *
     * @param startState The start state of the automa
     */
    public AsyncAutoma(STATE startState) {
      super(startState);
    }

    @Override
    protected void executeAction(Transition transition) {
      new Thread(transition.getAction()).start();
    }

}