package se.fredin.fxkcamel.jobengine.task;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;
import se.fredin.fxkcamel.jobengine.task.filter.FilterDatasetTask;
import se.fredin.fxkcamel.jobengine.task.join.JoinTask;
import se.fredin.fxkcamel.jobengine.task.join.OutData;
import se.fredin.fxkcamel.jobengine.task.join.RecordSelection;
import se.fredin.fxkcamel.jobengine.task.transform.TransformTask;
import se.fredin.fxkcamel.jobengine.task.union.UnionTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskCall {

    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionTask(newExchange, oldExchange).doExecuteTask();
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, RecordSelection recordSelection, OutData outData) {
        return new JoinTask(mainExchange, joiningExchange, recordSelection, outData).doExecuteTask();
    }

    public static <T extends FxKBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformTask(exchange, transformFunction).doExecuteTask();
    }

    public static Exchange filter(Exchange exchange, Predicate<List<String>> filterFunction) {
        return new FilterDatasetTask(exchange, filterFunction).doExecuteTask();
    }

}
