package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.filter.CsvFilterProcessor;
import se.fredin.llama.processor.join.FilterValidateAgainstBeansProcessor;
import se.fredin.llama.processor.join.JoinCollectionsProcessor;
import se.fredin.llama.processor.join.JoinKey;
import se.fredin.llama.processor.join.JoinType;
import se.fredin.llama.processor.transform.TransformProcessor;
import se.fredin.llama.processor.union.UnionProcessor;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Processors {

    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionProcessor(newExchange, oldExchange).doExecuteProcess();
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key) {
        return join(mainExchange, joiningExchange, Collections.singletonList(key), JoinType.INNER, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys, JoinType joinType) {
        return join(mainExchange, joiningExchange, keys, joinType, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key, JoinType joinType) {
        return join(mainExchange, joiningExchange, Collections.singletonList(key), joinType, Fields.ALL, Fields.ALL);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, JoinKey key, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return join(mainExchange, joiningExchange, Collections.singletonList(key), joinType, entity1Fields, entity2Fields);
    }

    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return new JoinCollectionsProcessor(mainExchange, joiningExchange, keys, joinType, ResultType.AS_IS, entity1Fields, entity2Fields).doExecuteProcess();
    }

    public static <T extends LlamaBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformProcessor<>(exchange, transformFunction).doExecuteProcess();
    }

    public static Exchange filter(Exchange exchange, Predicate<List<String>> filterFunction) {
        return new CsvFilterProcessor(exchange, filterFunction).doExecuteProcess();
    }

    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange) {
        return Processors.<T1, T2>filterValidateAgainst(mainExchange, joiningExchange, JoinType.INNER);
    }

    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype) {
        return filterValidateAgainst(mainExchange, joiningExchange, jointype, ResultType.AS_IS);
    }

    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype, ResultType resultType) {
        return new <T1, T2>FilterValidateAgainstBeansProcessor(mainExchange, joiningExchange, jointype, resultType).doExecuteProcess();
    }

}
