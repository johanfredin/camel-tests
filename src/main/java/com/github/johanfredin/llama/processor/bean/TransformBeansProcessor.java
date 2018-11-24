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
package com.github.johanfredin.llama.processor.bean;

import com.github.johanfredin.llama.bean.LlamaBean;
import org.apache.camel.Exchange;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Used to alter the data in a collection based in the passed in consumer function.
 * Works with a single exchange that is expected to contain a body of a llama bean collection.
 *
 * @param <T> any class extending {@link LlamaBean}
 */
public class TransformBeansProcessor<T extends LlamaBean> extends SimpleBeanProcessor<T> {

    private Consumer<T> function;

    /**
     * Create a new instance
     *
     * @param exchange the exchange to modify
     * @param function the transformBeans function to apply
     */
    public TransformBeansProcessor(Exchange exchange, Consumer<T> function) {
        super(exchange);
        setFunction(function);
    }

    /**
     * @param function the transformBeans function to apply
     */
    private void setFunction(Consumer<T> function) {
        this.function = function;
    }

    /**
     * @return the transformBeans function to apply
     */
    public Consumer<T> getFunction() {
        return function;
    }

    @Override
    public List<T> processData(List<T> beans) {
        return beans
                .stream()
                .peek(getFunction())
                .collect(Collectors.toList());
    }


}
