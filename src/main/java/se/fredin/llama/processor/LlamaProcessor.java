package se.fredin.llama.processor;

import org.apache.camel.Exchange;

public interface LlamaProcessor {

    Exchange doExecuteProcess();

    int getInitialRecords();

    int getProcessedRecords();

    void postExecute();

    void postCreate();

    String getTaskName();

    ResultType getResultType();
}
