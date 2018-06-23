package se.fredin.fxkcamel.jobengine.task;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.task.join.JoinTask;
import se.fredin.fxkcamel.jobengine.task.join.OutEntity;
import se.fredin.fxkcamel.jobengine.task.join.RecordSelection;
import se.fredin.fxkcamel.jobengine.task.union.UnionTask;

public class TaskCall {

    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionTask(newExchange, oldExchange).union();
    }

    public static Exchange join(Exchange mainExhange, Exchange joiningExchange, RecordSelection recordSelection, OutEntity outEntity) {
        return new JoinTask(mainExhange, joiningExchange, recordSelection, outEntity).join();
    }

}
