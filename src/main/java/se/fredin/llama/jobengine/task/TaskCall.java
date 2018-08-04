package se.fredin.llama.jobengine.task;

import org.apache.camel.Exchange;
import se.fredin.llama.jobengine.bean.FxKBean;
import se.fredin.llama.jobengine.task.filter.FilterDatasetTask;
import se.fredin.llama.jobengine.task.join.JoinKey;
import se.fredin.llama.jobengine.task.join.JoinTask;
import se.fredin.llama.jobengine.task.join.JoinType;
import se.fredin.llama.jobengine.task.transform.TransformTask;
import se.fredin.llama.jobengine.task.union.UnionTask;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskCall {

    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionTask(newExchange, oldExchange).doExecuteTask();
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key) {
        return join(mainExchange, joiningExchange, Arrays.asList(key), JoinType.INNER, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys, JoinType joinType) {
        return join(mainExchange, joiningExchange, keys, joinType, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key, JoinType joinType) {
        return join(mainExchange, joiningExchange, Arrays.asList(key), joinType, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return join(mainExchange, joiningExchange, Arrays.asList(key), joinType, entity1Fields, entity2Fields);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return new JoinTask(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields).doExecuteTask();
    }

    public static <T extends FxKBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformTask(exchange, transformFunction).doExecuteTask();
    }

    public static Exchange filter(Exchange exchange, Predicate<List<String>> filterFunction) {
        return new FilterDatasetTask(exchange, filterFunction).doExecuteTask();
    }


}
