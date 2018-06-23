package se.fredin.fxkcamel.jobengine.task;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Task {

    Exchange doExecuteTask();

    int getProcessedRecords();

    void postExecute();

    String getName();

}
