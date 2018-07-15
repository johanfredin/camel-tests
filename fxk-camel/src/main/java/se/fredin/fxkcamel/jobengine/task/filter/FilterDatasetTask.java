package se.fredin.fxkcamel.jobengine.task.filter;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.task.BaseTask;
import se.fredin.fxkcamel.jobengine.utils.Dataset;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterDatasetTask extends BaseTask {

    private Exchange exchange;
    private Predicate<List<String>> filterFunction;

    private int filteredRecords;

    public FilterDatasetTask() {
        super("Filter Task");
    }

    public FilterDatasetTask(Exchange exchange, Predicate<List<String>> filterFunction) {
        super("Filter Task");
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
    public Exchange doExecuteTask() {
        List<List<String>> records = new ArrayList<>(this.exchange.getIn(List.class));
        setProcessedRecords(records.size());

        List<List<String>> filteredRecords = new ArrayList<>();
        filteredRecords.addAll(
                records
                .stream()
                .filter(this.filterFunction)
                .collect(Collectors.toList())
        );

        this.filteredRecords = filteredRecords.size();
        this.exchange.getIn().setBody(filteredRecords);
        return this.exchange;
    }

    @Override
    public void postExecute() {
//        log.info(filteredRecords + " out of " + getProcessedRecords() + " filtered out");
        super.postExecute();
    }
}
