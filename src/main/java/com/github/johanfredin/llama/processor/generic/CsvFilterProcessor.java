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
package com.github.johanfredin.llama.processor.generic;

import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Takes a collection of maps from an exchange and filters them based on passed in filter {@link Predicate}.
 * The filtered result is then returned to the exchange.
 *
 * @author JFN
 */
public class CsvFilterProcessor extends SimpleGenericProcessor {

    private Predicate<Map<String, String>> filterFunction;

    /**
     * Create a new instance
     * should only be used in unit tests, hence protected
     *
     * @param filterFunction what to filter the collection on.
     * @param includeHeader  whether or not to include the header row in the resulting exchange
     */
    protected CsvFilterProcessor(Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        this(null, filterFunction, includeHeader);
    }

    /**
     * Create a new instance
     * should only be used in unit tests, hence protected
     *
     * @param exchange       the exchange with the body we want to filter
     * @param filterFunction what to filter the collection on.
     */
    public CsvFilterProcessor(Exchange exchange, Predicate<Map<String, String>> filterFunction) {
        this(exchange, filterFunction, false);
    }

    /**
     * Create a new instance
     * should only be used in unit tests, hence protected
     *
     * @param exchange       the exchange with the body we want to filter
     * @param filterFunction what to filter the collection on.
     * @param includeHeader  whether or not to include the header row in the resulting exchange
     */
    public CsvFilterProcessor(Exchange exchange, Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        this.exchange = exchange;
        this.includeHeader = includeHeader;
        this.filterFunction = filterFunction;
    }

    /**
     * @return the filter function to use on the collection
     */
    public Predicate<Map<String, String>> getFilterFunction() {
        return filterFunction;
    }

    /**
     * @param filterFunction the filter function to use on the collection
     */
    public void setFilterFunction(Predicate<Map<String, String>> filterFunction) {
        this.filterFunction = filterFunction;
    }

    /**
     * Filters the records in the list, protected so it could only be used in tests
     *
     * @param records the records to filter
     * @return the passed in records filtered
     */
    @Override
    public List<Map<String, String>> processData(List<Map<String, String>> records) {
        return records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList());
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

}
