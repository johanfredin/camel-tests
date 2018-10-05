package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base implementation for {@link LlamaProcessor} interface. Holds a Log4J instance and some helper methods
 */
public abstract class BaseProcessor implements LlamaProcessor {

    // Lazy way to tell post create and post process methods if these fields were altered or not.
    protected final byte UN_ALTERED_VALUE = -1;

    protected Logger log = LogManager.getLogger(this.getClass());
    protected ResultType resultType;

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

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public ResultType getResultType() {
        return this.resultType;
    }

    public abstract Exchange getResult();

    public abstract void process();

    @Override
    public String toString() {
        return "BaseProcessor{" +
                "log=" + log +
                ", resultType=" + resultType +
                ", initialRecords=" + initialRecords +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
