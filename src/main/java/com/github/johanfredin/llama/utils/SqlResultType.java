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
package com.github.johanfredin.llama.utils;

/**
 * Used when using an sql query with the {@link Endpoint} class.
 */
public enum SqlResultType {

    /**
     * We want to fetch the result one at a time.
     */
    ITERATE("true"),
    /**
     * We want to fetch all the results at onece.
     */
    ALL("false");

    private String label;

    SqlResultType(String label) {
        this.label = label;
    }

    /**
     * @return the string label with this enum entry
     */
    public String getLabel() {
        return label;
    }
}
