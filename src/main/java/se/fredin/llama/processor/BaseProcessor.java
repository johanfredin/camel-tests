package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseProcessor implements LlamaProcessor {

    protected Logger log = LogManager.getLogger(this.getClass());

    protected ResultType resultType = ResultType.AS_IS;

    protected int processedRecords;

    @Override
    public Exchange doExecuteTask() {
        postCreate();
        process();
        postExecute();
        return result();
    }

    protected abstract void process();

    protected abstract Exchange result();

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

    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public ResultType getResultType() {
        return this.resultType;
    }
}
