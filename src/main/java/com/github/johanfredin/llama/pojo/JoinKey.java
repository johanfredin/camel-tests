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
package com.github.johanfredin.llama.pojo;

import com.github.johanfredin.llama.processor.generic.JoinCollectionsProcessor;

/**
 * Used in {@link JoinCollectionsProcessor}. Holds 2 fields representing key in main
 * and key in joining where their values must match. Keys have to be strings but can
 * be created by concatenating a variety of fields (duh!)
 */
public class JoinKey {

    private String keyInMain;
    private String keyInJoining;

    /**
     * Create a new instance. Simplified version where we will assume that
     * the passed in key exists in both exchanges with the same name.
     * @param key the field that will be used as key in both exchanges.
     */
    public JoinKey(String key) {
        this(key, key);
    }

    /**
     * Create a new instance
     * @param keyInMain the representation of the key in the main exchange
     * @param keyInJoining the representation of the key in the joining exchange
     */
    public JoinKey(String keyInMain, String keyInJoining) {
        this.keyInMain = keyInMain;
        this.keyInJoining = keyInJoining;
    }

    /**
     * @return the representation of the key in the main exchange
     */
    public String getKeyInMain() {
        return keyInMain;
    }

    /**
     * @param keyInMain the representation of the key in the main exchange
     */
    public void setKeyInMain(String keyInMain) {
        this.keyInMain = keyInMain;
    }

    /**
     * @return the representation of the key in the joining exchange
     */
    public String getKeyInJoining() {
        return keyInJoining;
    }

    /**
     * @param keyInJoining the representation of the key in the joining exchange
     */
    public void setKeyInJoining(String keyInJoining) {
        this.keyInJoining = keyInJoining;
    }

    @Override
    public String toString() {
        return "JoinKey{" +
                "keyInMain='" + keyInMain + '\'' +
                ", keyInJoining='" + keyInJoining + '\'' +
                '}';
    }


}
