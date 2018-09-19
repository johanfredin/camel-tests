package se.fredin.llama.processor;

import org.apache.camel.Exchange;

public interface LlamaProcessor {

    Exchange doExecuteTask();

    int getProcessedRecords();

    void postExecute();

    void postCreate();

    String getTaskName();

    ResultType getResultType();
}
