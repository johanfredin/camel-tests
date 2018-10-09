package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.pojo.Fields;
import se.fredin.llama.pojo.JoinType;
import se.fredin.llama.pojo.Keys;
import se.fredin.llama.processor.bean.FilterValidateAgainstBeansProcessor;
import se.fredin.llama.processor.bean.TransformBeansProcessor;
import se.fredin.llama.processor.bean.UnionBeansProcessor;
import se.fredin.llama.processor.generic.CsvFilterProcessor;
import se.fredin.llama.processor.generic.JoinCollectionsProcessor;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Used as a static middle layer between camel {@link org.apache.camel.Route}s and the
 * {@link LlamaProcessor}s. Most processors(if not all) in camel needs to return some sort of
 * {@link Exchange}. All {@link LlamaProcessor} instances holds their own <b>doExecuteProcess</b> method that
 * always returns an exchange. Every method here calls invokes a new processor instance and returns that
 * instances <b>doExecuteProcess</b> method. So plain and simple, by invoking the llama processors from here we get less bloated routes.
 * @author johan
 */
public class Processors {

    /**
     * Invokes a new {@link UnionBeansProcessor} with the 2 exchanges passed in.
     * See the documentation of UnionBeansProcessor for more info.
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
     *     <li>JoinType={@link JoinType#INNER}</li>
     *     <li>entity1Fields={@link Fields#ALL}</li>
     *     <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL, false);
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets public static values. Those are.
     * <ul>
     *     <li>JoinType={@link JoinType#INNER}</li>
     *     <li>entity1Fields={@link Fields#ALL}</li>
     *     <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, boolean includeHeader) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL, includeHeader);
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets public static values. Those are.
     * <ul>
     *     <li>entity1Fields={@link Fields#ALL}</li>
     *     <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @param joinType what type of filterValidateAgainst to use.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with public static values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType) {
        return join(mainExchange, joiningExchange, keys, joinType, Fields.ALL, Fields.ALL, false);
    }

    /**
     * Create a new {@link JoinCollectionsProcessor} with the passed in params and return its <b>doExecuteProcess</b> method.
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @param joinType what type of filterValidateAgainst to use.
     * @param entity1Fields the fields we want to include from the main exchange in the resulting exchange.
     * @param entity2Fields the fields we want to include from the joining exchange in the resulting exchange.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()}
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        return join(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields, false);
    }

    /**
     * Create a new {@link JoinCollectionsProcessor} with the passed in params and return its <b>doExecuteProcess</b> method.
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @param joinType what type of filterValidateAgainst to use.
     * @param entity1Fields the fields we want to include from the main exchange in the resulting exchange.
     * @param entity2Fields the fields we want to include from the joining exchange in the resulting exchange.
     * @param includeHeader whether or not to include the header row in the result (public static is false)
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()}
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields, boolean includeHeader) {
        return new JoinCollectionsProcessor(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields, includeHeader).doExecuteProcess();
    }

    /**
     * Calls the {@link TransformBeansProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the transformFunction passed in.
     * Exchange body needs to be a collection of type {@link LlamaBean}.
     * @param exchange the exchange to transform
     * @param transformFunction the transform function.
     * @param <T> any type extending {@link LlamaBean}
     * @return the exchange passed in transformed.
     */
    public static <T extends LlamaBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformBeansProcessor<>(exchange, transformFunction).doExecuteProcess();
    }

    /**
     * Calls the {@link CsvFilterProcessor} on the passed in exchange, filtering
     * the body of the exchange according to passed in filterFunction.
     * @param exchange the exchange to transform
     * @param filterFunction the filter function.
     * @return the exchange passed in filtered.
     */
    public static Exchange filter(Exchange exchange, Predicate<Map<String, String>> filterFunction) {
        return filter(exchange, filterFunction, false);
    }

    /**
     * Calls the {@link CsvFilterProcessor} on the passed in exchange, filtering
     * the body of the exchange according to passed in filterFunction.
     * @param exchange the exchange to transform
     * @param filterFunction the filter function
     * @param includeHeader whether or not to include the header row (public static is false)
     * @return the exchange passed in filtered.
     */
    public static Exchange filter(Exchange exchange, Predicate<Map<String, String>> filterFunction, boolean includeHeader) {
        return new CsvFilterProcessor(exchange, filterFunction, includeHeader).doExecuteProcess();
    }

    /**
     * Reflective call to {@link #filterValidateAgainst(Exchange, Exchange, JoinType)}
     * Params not included for that method gets public static values. Those values are
     * <ul>
     *     <li>joinType={@link JoinType#INNER}</li>
     * </ul>
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     * @param mainExchange the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param <T1> any type extending {@link LlamaBean}
     * @param <T2> any type extending {@link LlamaBean}
     * @return {@link #filterValidateAgainst(Exchange, Exchange, JoinType)} with public static values for missing params.
     */
    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange) {
        return filterValidateAgainst(mainExchange, joiningExchange, JoinType.INNER);
    }

    /**
     * Invokes a new instance of {@link FilterValidateAgainstBeansProcessor} with the passed in params and returns it <b>doExecuteProcess</b> method
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     * @param mainExchange the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param jointype the type of join to use.
     * @param <T1> any type extending {@link LlamaBean}
     * @param <T2> any type extending {@link LlamaBean}
     * @return a call to {@link FilterValidateAgainstBeansProcessor#doExecuteProcess()} with passed in params.
     */
    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype) {
        return new <T1, T2>FilterValidateAgainstBeansProcessor(mainExchange, joiningExchange, jointype).doExecuteProcess();
    }

}
