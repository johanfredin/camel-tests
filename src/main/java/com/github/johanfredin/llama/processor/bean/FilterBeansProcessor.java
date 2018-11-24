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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filters a collection of llama beans based on the predicate passed in.
 * @param <T> any class extending {@link LlamaBean}
 */
public class FilterBeansProcessor<T extends LlamaBean> extends SimpleBeanProcessor<T> {

    private Predicate<T> function;

    /**
     * Create a new instance
     * @param exchange the exchange whose body we are going to filter
     * @param function the filter function to apply.
     */
    public FilterBeansProcessor(Exchange exchange, Predicate<T> function) {
        super(exchange);
        setFunction(function);
    }

    /**
     * @param function the filter function to apply.
     */
    public void setFunction(Predicate<T> function) {
        this.function = function;
    }

    /**
     * @return the filter function applied.
     */
    public Predicate<T> getFunction() {
        return function;
    }

    @Override
    public List<T> processData(List<T> beans) {
        return beans
                .stream()
                .filter(getFunction())
                .collect(Collectors.toList());

    }

}
