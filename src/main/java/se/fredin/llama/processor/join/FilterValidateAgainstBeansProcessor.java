package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.ResultType;
import se.fredin.llama.utils.LlamaUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filter processor that can be used to compare a collection of {@link LlamaBean}s against another.
 * Result can be filtered on one of {@link JoinType}s {@link JoinType#INNER}, {@link JoinType#LEFT_EXCLUDING}.
 * Useful when we have an exchange containing data that we either want to ensure exists in another exchange or is
 * unique in our exchange. Merging the data however is not possible since we can not at this stage know what fields exists
 * in the other llama bean collection. For that you would have to create your own process.
 * Data is matched using the {@link LlamaBean#getId()} property. Out data will always be based on that of exchange1
 * @param <T> The type expected to be in the main exchange (must extend {@link LlamaBean}
 * @param <T2> The type expected to be in the joining exchange (must extend {@link LlamaBean}
 */
public class FilterValidateAgainstBeansProcessor<T extends LlamaBean, T2 extends LlamaBean> extends AbstractJoinProcessor {

    protected FilterValidateAgainstBeansProcessor(JoinType joinType) {
        this.joinType = joinType;
    }

    public FilterValidateAgainstBeansProcessor(Exchange main, Exchange joining, JoinType joinType, ResultType resultType) {
        super(main, joining, joinType, resultType);
    }

    @Override
    public void postCreate() {
        super.postCreate();
        log.info("Result type=" + this.resultType);
    }

    @Override
    public Exchange doExecuteProcess() {
        Map<Serializable, List<T>> mainMap = LlamaUtils.asLlamaBeanMap(this.main);
        setInitialRecords(mainMap.size());
        postCreate();
        var result = join(mainMap, LlamaUtils.asLlamaBeanMap(this.joining));

        switch(getResultType()) {
            case LIST:
                this.main.getIn().setBody(result.values().
                        stream().
                        flatMap(List::stream).
                        collect(Collectors.toList()));
                break;
            default:
                this.main.getIn().setBody(result);
                break;
        }
        super.setProcessedRecords(result.size());
        postExecute();
        return this.main;
    }

    protected Map<Serializable, List<T>> join(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap) {
        switch (this.joinType) {
            case INNER:
                return innerOrExcludingJoin(mainMap, joiningMap, true);
            case LEFT_EXCLUDING:
                return innerOrExcludingJoin(mainMap, joiningMap, false);
        }
        throw new RuntimeException("Join type in task=" + getProcessorName() + " must be one of either{" + JoinType.INNER + ", " + JoinType.LEFT_EXCLUDING + "}");
    }

    private Map<Serializable, List<T>> innerOrExcludingJoin(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap, boolean isInnerJoin) {
        return mainMap.entrySet()
                .stream()
                .filter(entry -> isInnerJoin == joiningMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
