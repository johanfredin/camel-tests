/**
 * Copyright 2018 Johan Fredin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johanfredin.llama.processor.bean;

import org.apache.camel.Exchange;
import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.processor.AbstractJoinProcessor;
import com.github.johanfredin.llama.pojo.JoinType;
import com.github.johanfredin.llama.utils.LlamaUtils;

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
     */
    public FilterValidateAgainstBeansProcessor(Exchange main, Exchange joining, JoinType joinType) {
        super(main, joining, joinType);
    }

    @Override
    public void process() {
        Map<Serializable, List<T>> mainMap = LlamaUtils.asLlamaBeanMap(this.main);
        setInitialRecords(mainMap.size());
        var result = filterValidateAgainst(mainMap, LlamaUtils.asLlamaBeanMap(this.joining));
        super.setProcessedRecords(result.size());
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
