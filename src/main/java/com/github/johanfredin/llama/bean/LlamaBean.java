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
package com.github.johanfredin.llama.bean;

import java.io.Serializable;

/**
 * Interface that works with all the bean processors. Holds one method {@link #getId()}
 * of type {@link Serializable}. This so that it could also be persisted with JPA/Hibernate etc...
 * {@link #getId()} method is used by all the processor classes under the /bean package that operates
 * on collections/maps of llama beans to identify the records.
 *
 * @author johanfredin
 */
public interface LlamaBean extends Serializable {

    /**
     * The property (or concatenation of properties) that should be used
     * as the identifier of this instance.
     * @return the field qualifying as the identifier of the instance.
     */
    Serializable getId();

}
