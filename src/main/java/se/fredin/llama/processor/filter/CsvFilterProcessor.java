package se.fredin.llama.processor.filter;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CsvFilterProcessor extends BaseProcessor {

    private Exchange exchange;
    private Predicate<List<String>> filterFunction;

    private int filteredRecords;

    public CsvFilterProcessor() {

    }

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
    protected void process() {
        var records = ProcessorUtils.<List<String>>asTypedList(this.exchange);
        setProcessedRecords(records.size());

        var filteredRecords = records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList());

        this.filteredRecords = filteredRecords.size();
        this.exchange.getIn().setBody(filteredRecords);
    }

    @Override
    protected Exchange result() {
        return this.exchange;
    }

    @Override
    public void postExecute() {
        log.info(filteredRecords + " out of " + getProcessedRecords() + " filtered out");
        super.postExecute();
    }
}
