package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.utils.ProcessorUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filter processor that can be used to compare a collection of {@link LlamaBean}s against another.
 * Result can be filtered on one of {@link JoinType}s {@link JoinType#INNER}, {@link JoinType#LEFT_EXCLUDING}.
 * Useful if we either want to verify the data exists in another or does not exist in another collection.
 * Data is matched using the {@link LlamaBean#getId()} property. Out data will always be based on that of exchange1
 * @param <T> The type expected to be in the main exchange (must extend {@link LlamaBean}
 * @param <T2> The type expected to be in the joining exchange (must extend {@link LlamaBean}
 */
public class FilterValidateAgainstBeansProcessor<T extends LlamaBean, T2 extends LlamaBean> extends AbstractJoinProcessor {

    public FilterValidateAgainstBeansProcessor(JoinType joinType) {
        this.joinType = joinType;
    }

    public FilterValidateAgainstBeansProcessor(Exchange main, Exchange joining, JoinType joinType) {
        super(main, joining, joinType);
    }

    @Override
    public Exchange doExecuteTask() {
        this.main.getIn().setBody(join(ProcessorUtils.asLlamaBeanMap(this.main), ProcessorUtils.asLlamaBeanMap(this.joining)));
        return this.main;
    }

    protected Map<Serializable, List<T>> join(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap) {
        switch (this.joinType) {
            case INNER:
                return innerOrExcludingJoing(mainMap, joiningMap, true);
            case LEFT_EXCLUDING:
                return innerOrExcludingJoing(mainMap, joiningMap, false);
        }
        log.error("Join type in task=" + getTaskName() + " must be one of either{" + JoinType.INNER + ", " + JoinType.LEFT_EXCLUDING + "}");
        return Map.of();
    }

    private Map<Serializable, List<T>> innerOrExcludingJoing(Map<Serializable, List<T>> mainMap, Map<Serializable, List<T2>> joiningMap, boolean isInnerJoin) {
        return mainMap.entrySet()
                .stream()
                .filter(entry -> isInnerJoin == joiningMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
