package se.fredin.llama.processor.generic;

import org.apache.camel.Exchange;
import se.fredin.llama.utils.LlamaUtils;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CsvFilterProcessor extends GenericProcessor {

    private Exchange exchange;
    private Predicate<Map<String, String>> filterFunction;

    public CsvFilterProcessor() {
        super(false);
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

        var filteredRecords = records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList());

        if(super.includeHeader) {
            filteredRecords.add(0, getHeader(filteredRecords.get(0).keySet()));
        }

        this.exchange.getIn().setBody(filteredRecords);
        this.processedRecords = filteredRecords.size();
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

}
