/*
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
package com.github.johanfredin.llama.utils;

/**
 * Used with the {@link Endpoint} class to build string uris to I/O sources.
 * Each entry contains a string value that will be used in the uris.
 *
 * @author johan
 */
public enum InputType {

    /**
     * Source is from/to a file
     */
    FILE("file"),
    /**
     * Read from/write to a database with an sql.
     */
    SQL("sql"),
    /**
     * Consume from/produce to a JPA entity class.
     */
    JPA("jpa");

    private final String type;

    InputType(String type) {
        this.type = type;
    }

    /**
     * @return the string value of given entry
     */
    public String getType() {
        return type;
    }
}
