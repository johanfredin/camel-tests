package se.fredin.llama.processor.generic;

import org.apache.camel.Exchange;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CsvFilterProcessor extends GenericProcessor {

    private Exchange exchange;
    private Predicate<Map<String, String>> filterFunction;

    protected CsvFilterProcessor() {
        super(false);
    }

    protected CsvFilterProcessor(Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        super(includeHeader);
        this.filterFunction = filterFunction;
    }

    public CsvFilterProcessor(Exchange exchange, Predicate<Map<String, String>> filterFunction) {
        this(exchange, filterFunction, false);
    }

    public CsvFilterProcessor(Exchange exchange, Predicate<Map<String, String>> filterFunction, boolean includeHeaders) {
        super(includeHeaders);
        this.exchange = exchange;
        this.filterFunction = filterFunction;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Predicate<Map<String, String>> getFilterFunction() {
        return filterFunction;
    }

    public void setFilterFunction(Predicate<Map<String, String>> filterFunction) {
        this.filterFunction = filterFunction;
    }

    @Override
    public void process() {
        var records = LlamaUtils.asLinkedListOfMaps(this.exchange);
        this.initialRecords = records.size();

        var filteredRecords = filterRecords(records);

        this.exchange.getIn().setBody(filteredRecords);
        this.processedRecords = filteredRecords.size();
    }

    /**
     * Filters the records in the list, protected so it could only be used in tests
     * @param records the records to filter
     * @return the passed in records filtered
     */
    protected List<Map<String, String>> filterRecords(List<Map<String,String>> records) {
        var filteredRecords = records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList());

        if(super.includeHeader) {
            filteredRecords.add(0, getHeader(filteredRecords.get(0).keySet()));
        }

        return filteredRecords;
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

}
