package se.fredin.llama.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseProcessor implements LlamaProcessor {

    protected Logger log = LogManager.getLogger(this.getClass());

    protected int processedRecords;

    public BaseProcessor() {
        postCreate();
    }

    @Override
    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    public int incProcessedRecords() {
        return this.processedRecords++;
    }

    @Override
    public void postCreate() {
        log.info(getTaskName() + " initiated");
    }

    @Override
    public void postExecute() {
        log.info(getTaskName() + " completed");
        log.info(getProcessedRecords() + " records processed");
    }

    @Override
    public String getTaskName() {
        return this.getClass().getSimpleName();
    }
}
