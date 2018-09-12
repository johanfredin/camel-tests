package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.utils.ProcessorUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Similar to the {@link JoinCollectionsProcessor} except here we join 2 collections of type {@link se.fredin.llama.bean.LlamaBean}
 * Join will be based on the {@link LlamaBean#getId()} property. We are not able to combine the data in this join. We can only keep
 * data from one of the exchanges.
 */
public class JoinLlamaBeansProcessor<T extends LlamaBean> extends AbstractJoinProcessor {

    private OutData outData;

    public JoinLlamaBeansProcessor(JoinType joinType, OutData outData) {
        this.joinType = joinType;
        this.outData = outData;
    }

    public JoinLlamaBeansProcessor(Exchange main, Exchange joining, JoinType joinType, OutData outData) {
        super(main, joining, joinType);
        this.outData = outData;
    }

    public OutData getOutData() {
        return outData;
    }

    public void setOutData(OutData outData) {
        this.outData = outData;
    }

    @Override
    public Exchange doExecuteTask() {
        this.main.getIn().setBody(join(ProcessorUtils.asLlamaBeanMap(this.main), ProcessorUtils.asLlamaBeanMap(this.joining)));
        return this.main;
    }

    protected Map<Object, List<T>> join(Map<Object, List<T>> mainMap, Map<Object, List<T>> joiningMap) {
        if(joinType == )

        switch (this.joinType) {
            case INNER:
                return innerJoin(mainMap, joiningMap);
            case LEFT_EXCLUDING:
                return excludingJoin(mainMap, joiningMap);
            case RIGHT_EXCLUDING:
                return excludingJoin(joiningMap, mainMap);
        }

        return Map.of();
    }

    private Map<Object, List<T>> innerJoin(Map<Object, List<T>> mainMap, Map<Object, List<T>> joiningMap) {
        return mainMap.entrySet()
                .stream()
                .filter(entry -> joiningMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private Map<Object, List<T>> excludingJoin(Map<Object, List<T>> mainMap, Map<Object, List<T>> joiningMap) {
        return mainMap.entrySet()
                .stream()
                .filter(entry -> !joiningMap.containsKey(entry.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

}
