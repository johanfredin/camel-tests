package com.github.johanfredin.llama.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base implementation for {@link LlamaProcessor} interface. Holds a Log4J instance and some helper methods
 */
public abstract class BaseProcessor implements LlamaProcessor {

    // Lazy way to tell post create and post process methods if these fields were altered or not.
    protected final byte UN_ALTERED_VALUE = -1;

    protected Logger log = LogManager.getLogger(this.getClass());

    protected int initialRecords = UN_ALTERED_VALUE;
    protected int processedRecords = UN_ALTERED_VALUE;

    /**
     * Used when wanting to keep track of processed records.
     * Should be called before execution to determine amount of records to process.
     * @param initialRecords the initial amount of records to process.
     */
    public void setInitialRecords(int initialRecords) {
        this.initialRecords = initialRecords;
    }

    /**
     * @return the initial records to process.
     */
    public int getInitialRecords() {
        return initialRecords;
    }

    /**
     * @return the amount of processed records.
     */
    public int getProcessedRecords() {
        return processedRecords;
    }

    /**
     * Should be called after execution of process to tell how many records were processed.
     * @param processedRecords the amount of records processed.
     */
    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    /**
     * Increments the amount of processed records by one.
     * @return #getProcessedRecords+1
     */
    public int incProcessedRecords() {
        return this.processedRecords++;
    }

    /**
     * Creates a map of passed in collection where each key/value=header[i]
     * @param header the header to create a map from.
     * @return a map of passed in collection where each key/value=header[i]
     */
    public Map<String,String> getHeader(Collection<String> header) {
        return header
                .stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity(),
                        (a, b) -> b, LinkedHashMap::new));
    }

    @Override
    public Exchange doExecuteProcess() {
        postCreate();
        process();
        postExecute();
        return getResult();
    }

    @Override
    public void postCreate() {
        log.info("=======================================");
        log.info(getProcessorName() + " initiated");
        if(this.initialRecords != UN_ALTERED_VALUE) {
            log.info("Initial records=" + this.initialRecords);
        }
    }

    @Override
    public void postExecute() {
        log.info(getProcessorName() + " completed");
        if(this.processedRecords != UN_ALTERED_VALUE) {
            log.info("Resulting records=" + getProcessedRecords());
        }
        log.info("=======================================");
    }

    @Override
    public String getProcessorName() {
        return this.getClass().getSimpleName();
    }

    /**
     * The exchange that contains the resulting collection of this processor.
     * Will be used as the return value in {@link #doExecuteProcess()} so
     * all subclasses must provide a valid exchange to return.
     * @return The exchange that contains the resulting collection of this processor.
     */
    public abstract Exchange getResult();

    /**
     * Put all processing logic in here, including
     * setting initial and resulting records and
     * assigning the resulting collection to the exchange
     * to return. When extending this class {@link #doExecuteProcess()} is
     * executed (unless overwritten) using the following steps:
     * <ul>
     *     <li>call {@link #postCreate()}</li>
     *     <li>call this method</li>
     *     <li>call {@link #postExecute()}</li>
     * </ul>
     * Return value of {@link #doExecuteProcess()} = {@link #getResult()}
     */
    public abstract void process();

    @Override
    public String toString() {
        return "BaseProcessor{" +
                ", initialRecords=" + initialRecords +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
