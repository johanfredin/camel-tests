package se.fredin.llama.processor.filter;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.LlamaUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CsvFilterProcessor extends BaseProcessor {

    private Exchange exchange;
    private Predicate<List<String>> filterFunction;

    private int filteredRecords;

    public CsvFilterProcessor() {}

    public CsvFilterProcessor(Exchange exchange, Predicate<List<String>> filterFunction) {
        this.exchange = exchange;
        this.filterFunction = filterFunction;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Predicate<List<String>> getFilterFunction() {
        return filterFunction;
    }

    public void setFilterFunction(Predicate<List<String>> filterFunction) {
        this.filterFunction = filterFunction;
    }

    public int getFilteredRecords() {
        return filteredRecords;
    }

    public void setFilteredRecords(int filteredRecords) {
        this.filteredRecords = filteredRecords;
    }

    @Override
    public void process() {
        var records = LlamaUtils.<List<String>>asTypedList(this.exchange);
        this.initialRecords = records.size();

        var filteredRecords = records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList());

        this.filteredRecords = filteredRecords.size();
        this.exchange.getIn().setBody(filteredRecords);
        this.processedRecords = filteredRecords.size();
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

    @Override
    public void postExecute() {
        super.postExecute();
        log.info(filteredRecords + " out of " + getProcessedRecords() + " filtered out");
    }
}
