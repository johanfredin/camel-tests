package se.fredin.fxkcamel.jobengine.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseTask implements Task {

    protected Logger log = LogManager.getLogger(this.getClass());

    protected int processedRecords;

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
    public void postExecute() {
        log.info(getName(), "Task completed");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
