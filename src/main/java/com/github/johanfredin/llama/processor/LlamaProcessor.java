/**
 * Copyright 2018 Johan Fredin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johanfredin.llama.processor;

import org.apache.camel.Exchange;

/**
 * Helper interface for all custom llama processors.
 */
public interface LlamaProcessor {

    /**
     * Should be called before all processing begins.
     * Preferably post construct.
     */
    void postCreate();

    /**
     * Useful as a summary of what has happened.
     * Call this after processing.
     */
    void postExecute();

    /**
     * Main method of the processor. All logic should take place in here.
     * Camel requires that processors returns an exchange to pass to the next
     * step in the route.
     * @return the modified exchange
     */
    Exchange doExecuteProcess();

    /**
     * @return the name of this processor. Useful for logging.
     */
    String getProcessorName();

}
