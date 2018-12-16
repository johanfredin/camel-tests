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
 * Used with the {@link com.github.johanfredin.llama.LlamaRoute} class to build source uris.
 * Each entry holds a string value that will be used in the uris.
 *
 * @author johan
 */
public enum InputOptions {

    /**
     * Specify we want <b>noop=true</b> e.g keep the file we are reading from.
     */
    KEEP("noop=true"),
    /**
     * Specify we don't want to keep the file we are reading from e.g <b>noop=false</b>
     */
    DISCARD("noop=false");

    private String option;

    InputOptions(String option) {
        this.option = option;
    }

    /**
     * @return the string value of given entry
     */
    public String getOption() {
        return option;
    }
}
