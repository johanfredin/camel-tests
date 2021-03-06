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
package com.github.johanfredin.llama.pojo;

import com.github.johanfredin.llama.processor.bean.FilterValidateAgainstBeansProcessor;
import com.github.johanfredin.llama.processor.generic.JoinCollectionsProcessor;

/**
 * Different types of joins that can be used. {@link FilterValidateAgainstBeansProcessor} and
 * {@link JoinCollectionsProcessor} uses an arbitrary amount of these keys. The naming convention
 * is taken from the SQL language and has the same logic with some minor twists.
 */
public enum JoinType {

    /**
     * Look for matching records on the joining collection.
     * Records will be added with empty string values if not
     * found in joining collection.
     */
    LEFT,
    /**
     * Look for matching records in the main collection. In this
     * case the joining collection will be come the main. Records will
     * be added with empty string values if not found in main collection.
     */
    RIGHT,
    /**
     * Look for matching records in the joining collection. This type
     * requires that a record with the same Id value exists in the joining
     * collection of it will be excluded altogether.
     */
    INNER,
    OUTER,
    /**
     * Records can only exist in the main collection.
     */
    LEFT_EXCLUDING,
    /**
     * Records can only exist in the joining collection.
     */
    RIGHT_EXCLUDING,

}
