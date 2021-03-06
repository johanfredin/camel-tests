/*
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
package com.github.johanfredin.llama.processor;

import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.pojo.Fields;
import com.github.johanfredin.llama.pojo.JoinType;
import com.github.johanfredin.llama.pojo.Keys;
import com.github.johanfredin.llama.processor.bean.FilterBeansProcessor;
import com.github.johanfredin.llama.processor.bean.FilterValidateAgainstBeansProcessor;
import com.github.johanfredin.llama.processor.bean.TransformBeansProcessor;
import com.github.johanfredin.llama.processor.bean.UnionBeansProcessor;
import com.github.johanfredin.llama.processor.generic.CsvFilterProcessor;
import com.github.johanfredin.llama.processor.generic.CsvTransformProcessor;
import com.github.johanfredin.llama.processor.generic.JoinCollectionsProcessor;
import com.github.johanfredin.llama.processor.generic.MergeCollectionsProcessor;
import org.apache.camel.Exchange;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Used as a static middle layer between camel {@link org.apache.camel.Route}s and the
 * {@link LlamaProcessor}s. Most processors(if not all) in camel needs to return some sort of
 * {@link Exchange}. All {@link LlamaProcessor} instances holds their own <b>doExecuteProcess</b> method that
 * always returns an exchange. Every method here calls invokes a new processor instance and returns that
 * instances <b>doExecuteProcess</b> method. So plain and simple, by invoking the llama processors from here we get less bloated routes.
 *
 * @author johan
 */
public class Processors {

    /**
     * Invokes a new {@link UnionBeansProcessor} with the 2 exchanges passed in.
     * See the documentation of UnionBeansProcessor for more info.
     *
     * @param oldExchange the main exchange.
     * @param newExchange the exchange to aggregate.
     * @return The old exchange aggregated with the new exchange.
     */
    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionBeansProcessor(newExchange, oldExchange).doExecuteProcess();
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets public static values. Those are.
     * <ul>
     * <li>JoinType={@link JoinType#INNER}</li>
     * <li>entity1Fields={@link Fields#ALL}</li>
     * <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     *
     * @param mainExchange    the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys            the keys. Must exist as fields in both exchanges.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL, false);
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets public static values. Those are.
     * <ul>
     * <li>JoinType={@link JoinType#INNER}</li>
     * <li>entity1Fields={@link Fields#ALL}</li>
     * <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     *
     * @param mainExchange    the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys            the keys. Must exist as fields in both exchanges.
     * @param includeHeader   whether to include the header row or not.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, boolean includeHeader) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL, includeHeader);
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets public static values. Those are.
     * <ul>
     * <li>entity1Fields={@link Fields#ALL}</li>
     * <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     *
     * @param mainExchange    the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys            the keys. Must exist as fields in both exchanges.
     * @param joinType        what type of filterValidateAgainst to use.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType) {
        return join(mainExchange, joiningExchange, keys, joinType, Fields.ALL, Fields.ALL, false);
    }

    /**
     * Create a new {@link JoinCollectionsProcessor} with the passed in params and return its <b>doExecuteProcess</b> method.
     *
     * @param mainExchange    the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys            the keys. Must exist as fields in both exchanges.
     * @param joinType        what type of filterValidateAgainst to use.
     * @param entity1Fields   the fields we want to include from the main exchange in the resulting exchange.
     * @param entity2Fields   the fields we want to include from the joining exchange in the resulting exchange.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()}
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return join(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields, false);
    }

    /**
     * Create a new {@link JoinCollectionsProcessor} with the passed in params and return its <b>doExecuteProcess</b> method.
     *
     * @param mainExchange    the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys            the keys. Must exist as fields in both exchanges.
     * @param joinType        what type of filterValidateAgainst to use.
     * @param entity1Fields   the fields we want to include from the main exchange in the resulting exchange.
     * @param entity2Fields   the fields we want to include from the joining exchange in the resulting exchange.
     * @param includeHeader   whether or not to include the header row in the result (public static is false)
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()}
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields, boolean includeHeader) {
        return new JoinCollectionsProcessor(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields, includeHeader).doExecuteProcess();
    }

    /**
     * Calls the {@link TransformBeansProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the transformFunction passed in.
     * Exchange body needs to be a collection of type {@link LlamaBean}.
     *
     * @param exchange          the exchange to transformBeans
     * @param transformFunction the transformBeans function.
     * @param <T>               any type extending {@link LlamaBean}
     * @return the exchange passed in transformed.
     */
    public static <T extends LlamaBean> Exchange transformBeans(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformBeansProcessor<>(exchange, transformFunction).doExecuteProcess();
    }

    /**
     * Calls the {@link CsvTransformProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the transformFunction passed in.
     * Header will not be included.
     * Exchange body needs to be a collection of type {@link Map}s with key/value={@link String}.
     *
     * @param exchange          the exchange to transformBeans
     * @param transformFunction the transformBeans function.
     * @return the exchange passed in transformed.
     */
    public static Exchange transformCollection(Exchange exchange, Consumer<Map<String, String>> transformFunction) {
        return transformCollection(exchange, transformFunction, false);
    }

    /**
     * Calls the {@link CsvTransformProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the transformFunction passed in.
     * Exchange body needs to be a collection of type {@link Map}s with key/value={@link String}.
     *
     * @param exchange          the exchange to transformBeans
     * @param transformFunction the transformBeans function.
     * @param includeHeader     whether or not to include the header row in the result.
     * @return the exchange passed in transformed.
     */
    public static Exchange transformCollection(Exchange exchange, Consumer<Map<String, String>> transformFunction, boolean includeHeader) {
        return new CsvTransformProcessor(exchange, transformFunction, includeHeader).doExecuteProcess();
    }

    /**
     * Calls the {@link FilterBeansProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the filterFunction passed in.
     * Exchange body needs to be a collection of type {@link LlamaBean}.
     *
     * @param exchange       the exchange to transformBeans
     * @param filterFunction how to filter the bean collection.
     * @param <T>            any type extending {@link LlamaBean}
     * @return the exchange passed in transformed.
     */
    public static <T extends LlamaBean> Exchange filterBeans(Exchange exchange, Predicate<T> filterFunction) {
        return new FilterBeansProcessor<>(exchange, filterFunction).doExecuteProcess();
    }

    /**
     * Creates a new {@link CsvFilterProcessor} without a resulting header and returns its doExecuteProcess method
     *
     * @param exchange       the exchange to filter
     * @param filterFunction the filter function (hint: use lambda!)
     * @return the exchange with a filtered body
     */
    public static Exchange filterCollection(Exchange exchange, Predicate<Map<String, String>> filterFunction) {
        return filterCollection(exchange, filterFunction, false);
    }

    /**
     * Creates a new {@link CsvFilterProcessor} and returns its doExecuteProcess method
     *
     * @param exchange       the exchange to filter
     * @param filterFunction the filter function (hint: use lambda!)
     * @param includeHeader  whether or not to make the first entry in the resulting collection the header row
     * @return the exchange with a filtered body
     */
    public static Exchange filterCollection(Exchange exchange, Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        return new CsvFilterProcessor(exchange, filterFunction, includeHeader).doExecuteProcess();
    }

    /**
     * Reflective call to {@link #filterValidateAgainst(Exchange, Exchange, JoinType)}
     * Params not included for that method gets public static values. Those values are
     * <ul>
     * <li>joinType={@link JoinType#INNER}</li>
     * </ul>
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     *
     * @param mainExchange    the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param <T1>            any type extending {@link LlamaBean}
     * @param <T2>            any type extending {@link LlamaBean}
     * @return {@link #filterValidateAgainst(Exchange, Exchange, JoinType)} with public static values for missing params.
     */
    public static <T1 extends LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange) {
        return filterValidateAgainst(mainExchange, joiningExchange, JoinType.INNER);
    }

    /**
     * Invokes a new instance of {@link FilterValidateAgainstBeansProcessor} with the passed in params and returns it <b>doExecuteProcess</b> method
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     *
     * @param mainExchange    the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param jointype        the type of join to use.
     * @param <T1>            any type extending {@link LlamaBean}
     * @param <T2>            any type extending {@link LlamaBean}
     * @return a call to {@link FilterValidateAgainstBeansProcessor#doExecuteProcess()} with passed in params.
     */
    public static <T1 extends LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype) {
        return new <T1, T2>FilterValidateAgainstBeansProcessor(mainExchange, joiningExchange, jointype).doExecuteProcess();
    }

    /**
     * Invokes a new instance of {@link MergeCollectionsProcessor} with the passed in params and returns its <b>doExecuteProcess</b> method.
     * For more info see the {@link MergeCollectionsProcessor} doc.
     * @param mainExchange the main exchange
     * @param mergingExchange the exchange to merge into the main exchange
     * @return the 2 exchanges merged
     */
    public static Exchange merge(Exchange mainExchange, Exchange mergingExchange) {
        return merge(mainExchange, mergingExchange, false);
    }

    /**
     * Invokes a new instance of {@link MergeCollectionsProcessor} with the passed in params and returns its <b>doExecuteProcess</b> method.
     * For more info see the {@link MergeCollectionsProcessor} doc.
     * @param mainExchange the main exchange
     * @param mergingExchange the exchange to merge into the main exchange
     * @param includeHeader whether or not to include the header in the result.
     * @return the 2 exchanges merged
     */
    public static Exchange merge(Exchange mainExchange, Exchange mergingExchange, boolean includeHeader) {
        return new MergeCollectionsProcessor(mainExchange, mergingExchange, includeHeader).doExecuteProcess();
    }

}
