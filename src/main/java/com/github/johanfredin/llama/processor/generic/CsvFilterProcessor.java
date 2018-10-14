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

    private Exchange exchange;
    private Predicate<Map<String, String>> filterFunction;

    /**
     * Create a new instance
     * should only be used in unit tests, hence protected
     *
     * @param filterFunction what to filter the collection on.
     * @param includeHeader  whether or not to include the header row in the resulting exchange
     */
    protected CsvFilterProcessor(Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        this.includeHeader = includeHeader;
        this.filterFunction = filterFunction;
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
        this.includeHeader = includeHeader;
        this.exchange = exchange;
        this.filterFunction = filterFunction;
    }

    /**
     * @return the exchange containing the body we want to filter
     */
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange containing the body we want to filter
     */
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
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
