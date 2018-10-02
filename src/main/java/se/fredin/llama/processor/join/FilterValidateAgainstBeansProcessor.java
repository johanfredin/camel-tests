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

    /**
     * Create a new instance. Declared protected so should only be used with unit tests or
     * other testing purposes.
     * @param joinType that type of filterValidateAgainst to use.
     */
    protected FilterValidateAgainstBeansProcessor(JoinType joinType) {
        this.joinType = joinType;
    }

    /**
     * Create a new instance.
     * @param main the main exchange.
     * @param joining the joining exchange we want to compare the main exchange with.
     * @param joinType what type of filterValidateAgainst to use (one of #INNER, #LEFT_EXCLUDING)
     * @param resultType the type we want the result to be returned as.
     */
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
        var result = filterValidateAgainst(mainMap, LlamaUtils.asLlamaBeanMap(this.joining));

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

    /**
     * Determines the join type and calls internal {@link #filterValidateAgainst(Map, Map, boolean)} method.
     * @param mainMap the map with the data from the main exchange.
     * @param joiningMap the map with the data from the joining exchange.
     * @return the main map validated against the joining map.
     */
    protected Map<Serializable, List<T>> filterValidateAgainst(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap) {
        switch (this.joinType) {
            case INNER:
                return filterValidateAgainst(mainMap, joiningMap, true);
            case LEFT_EXCLUDING:
                return filterValidateAgainst(mainMap, joiningMap, false);
        }
        throw new RuntimeException("Join type in task=" + getProcessorName() + " must be one of either{" + JoinType.INNER + ", " + JoinType.LEFT_EXCLUDING + "}");
    }

    /**
     * Validates the main data against the joining based on the join type this processor was given.
     * @param mainMap the map with the data from the main exchange.
     * @param joiningMap the map with the data from the joining exchange.
     * @param isInnerJoin if true then inner join logic is applied. #LEFT_EXCLUDING otherwise.
     * @return the main map validated against the joining map.
     */
    private Map<Serializable, List<T>> filterValidateAgainst(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap, boolean isInnerJoin) {
        return mainMap.entrySet()
                .stream()
                .filter(entry -> isInnerJoin == joiningMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
