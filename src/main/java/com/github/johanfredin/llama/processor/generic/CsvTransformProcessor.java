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
package com.github.johanfredin.llama.processor.generic;

import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Used to modify the content of an exchanges body that is expected to be a List of Maps.
 * The collection is iterated and for each entry we modify it based on the {@link #transformFunction}
 * property within this class.
 *
 * @author johan fredin
 */
public class CsvTransformProcessor extends SimpleGenericProcessor {

    private Consumer<Map<String, String>> transformFunction;

    /**
     * Create a new instance
     *
     * @param transformFunction the function that we will use to modify the content.
     * @param includeHeader     whether or not to include the header in the result
     */
    protected CsvTransformProcessor(Consumer<Map<String, String>> transformFunction, boolean includeHeader) {
        this(null, transformFunction, includeHeader);
    }

    /**
     * Create a new instance
     *
     * @param exchange          the exchange containing the collection to modify.
     * @param transformFunction the function that we will use to modify the content.
     * @param includeHeader     whether or not to include the header in the result
     */
    public CsvTransformProcessor(Exchange exchange, Consumer<Map<String, String>> transformFunction, boolean includeHeader) {
        super(exchange);
        setIncludeHeader(includeHeader);
        setTransformFunction(transformFunction);
    }

    /**
     * @return the transform function used.
     */
    public Consumer<Map<String, String>> getTransformFunction() {
        return transformFunction;
    }

    /**
     * @param transformFunction the transform function to use.
     */
    public void setTransformFunction(Consumer<Map<String, String>> transformFunction) {
        this.transformFunction = transformFunction;
    }

    @Override
    public List<Map<String, String>> processData(List<Map<String, String>> records) {
        return records.
                stream()
                .peek(getTransformFunction())
                .collect(Collectors.toList());
    }

}
