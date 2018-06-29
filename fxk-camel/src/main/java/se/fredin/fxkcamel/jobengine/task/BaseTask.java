package se.fredin.fxkcamel.jobengine.task;

public abstract class BaseTask implements Task {

//    protected Logger log = LogManager.getLogger(this.getClass());

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
//        log.info("Task completed");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
