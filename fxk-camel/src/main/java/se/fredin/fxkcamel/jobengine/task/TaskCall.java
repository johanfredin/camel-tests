package se.fredin.fxkcamel.jobengine.task;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;
import se.fredin.fxkcamel.jobengine.task.filter.FilterDatasetTask;
import se.fredin.fxkcamel.jobengine.task.join.JoinKey;
import se.fredin.fxkcamel.jobengine.task.join.JoinTask;
import se.fredin.fxkcamel.jobengine.task.join.JoinType;
import se.fredin.fxkcamel.jobengine.task.transform.TransformTask;
import se.fredin.fxkcamel.jobengine.task.union.UnionTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskCall {

    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionTask(newExchange, oldExchange).doExecuteTask();
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys, JoinType joinType, String[] entity1Fields, String[] entity2Fields) {
        return new JoinTask(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields).doExecuteTask();
    }

    public static <T extends FxKBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformTask(exchange, transformFunction).doExecuteTask();
    }

    public static Exchange filter(Exchange exchange, Predicate<List<String>> filterFunction) {
        return new FilterDatasetTask(exchange, filterFunction).doExecuteTask();
    }

}
