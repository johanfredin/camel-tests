package se.fredin.llama.processor;

import org.apache.camel.Exchange;
import se.fredin.llama.bean.LlamaBean;
import se.fredin.llama.processor.filter.CsvFilterProcessor;
import se.fredin.llama.processor.join.FilterValidateAgainstBeansProcessor;
import se.fredin.llama.processor.join.JoinCollectionsProcessor;
import se.fredin.llama.processor.join.JoinType;
import se.fredin.llama.processor.transform.TransformProcessor;
import se.fredin.llama.processor.union.UnionProcessor;

import java.util.List;
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
     * Invokes a new {@link UnionProcessor} with the 2 exchanges passed in.
     * See the documentation of UnionProcessor for more info.
     * @param oldExchange the main exchange.
     * @param newExchange the exchange to aggregate.
     * @return The old exchange aggregated with the new exchange.
     */
    public static Exchange union(Exchange oldExchange, Exchange newExchange) {
        return new UnionProcessor(newExchange, oldExchange).doExecuteProcess();
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets default values. Those are.
     * <ul>
     *     <li>JoinType={@link JoinType#INNER}</li>
     *     <li>entity1Fields={@link Fields#ALL}</li>
     *     <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with default values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys) {
        return join(mainExchange, joiningExchange, keys, JoinType.INNER, Fields.ALL, Fields.ALL);
    }

    /**
     * Reflective call to {@link #join(Exchange, Exchange, Keys, JoinType, Fields, Fields)}
     * Params not passed in gets default values. Those are.
     * <ul>
     *     <li>entity1Fields={@link Fields#ALL}</li>
     *     <li>entity2Fields={@link Fields#ALL}</li>
     * </ul>
     * @param mainExchange the main exchange.
     * @param joiningExchange the exchange we are joining in.
     * @param keys the keys. Must exist as fields in both exchanges.
     * @param joinType what type of filterValidateAgainst to use.
     * @return {@link JoinCollectionsProcessor#doExecuteProcess()} with default values for joinType, entity1Fields, entity2Fields.
     */
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType) {
        return join(mainExchange, joiningExchange, keys, joinType, Fields.ALL, Fields.ALL);
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
        return join(mainExchange, joiningExchange, keys, joinType, entity1Fields, entity2Fields, ResultType.AS_IS);
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
    public static Exchange join(Exchange mainExchange, Exchange joiningExchange, Keys keys, JoinType joinType, Fields entity1Fields, Fields entity2Fields, ResultType resultType) {
        return new JoinCollectionsProcessor(mainExchange, joiningExchange, keys, joinType, resultType, entity1Fields, entity2Fields).doExecuteProcess();
    }

    /**
     * Calls the {@link TransformProcessor#doExecuteProcess()} on the passed in exchange
     * modifying the content of its body with the transformFunction passed in.
     * Exchange body needs to be a collection of type {@link LlamaBean}.
     * @param exchange the exchange to transform
     * @param transformFunction the transform function.
     * @param <T> any type extending {@link LlamaBean}
     * @return the exchange passed in transformed.
     */
    public static <T extends LlamaBean> Exchange transform(Exchange exchange, Consumer<T> transformFunction) {
        return new TransformProcessor<>(exchange, transformFunction).doExecuteProcess();
    }

    /**
     * Calls the {@link CsvFilterProcessor} on the passed in exchange, filtering
     * the body of the exchange according to passed in filterFunction.
     * @param exchange the exchange to transform
     * @param filterFunction the filter function.
     * @return the exchange passed in filtered.
     */
    public static Exchange filter(Exchange exchange, Predicate<List<String>> filterFunction) {
        return new CsvFilterProcessor(exchange, filterFunction).doExecuteProcess();
    }

    /**
     * Reflective call to {@link #filterValidateAgainst(Exchange, Exchange, JoinType, ResultType)}
     * Params not included for that method gets default values. Those values are
     * <ul>
     *     <li>joinType={@link JoinType#INNER}</li>
     *     <li>resultType={@link ResultType#AS_IS}</li>
     * </ul>
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     * @param mainExchange the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param <T1> any type extending {@link LlamaBean}
     * @param <T2> any type extending {@link LlamaBean}
     * @return {@link #filterValidateAgainst(Exchange, Exchange, JoinType, ResultType)} with default values for missing params.
     */
    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange) {
        return Processors.<T1, T2>filterValidateAgainst(mainExchange, joiningExchange, JoinType.INNER, ResultType.AS_IS);
    }

    /**
     * Reflective call to {@link #filterValidateAgainst(Exchange, Exchange, JoinType, ResultType)}
     * Params not included for that method gets default values. Those values are
     * <ul>
     *     <li>resultType={@link ResultType#AS_IS}</li>
     * </ul>
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     * @param mainExchange the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param jointype the type of join to use
     * @param <T1> any type extending {@link LlamaBean}
     * @param <T2> any type extending {@link LlamaBean}
     * @return {@link #filterValidateAgainst(Exchange, Exchange, JoinType, ResultType)} with default values for missing params.
     */
    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype) {
        return filterValidateAgainst(mainExchange, joiningExchange, jointype, ResultType.AS_IS);
    }

    /**
     * Invokes a new instance of {@link FilterValidateAgainstBeansProcessor} with the passed in params and returns it <b>doExecuteProcess</b> method
     * Both exchanges needs to have a body with a collection of type {@link LlamaBean}.
     * For more info see {@link FilterValidateAgainstBeansProcessor}
     * @param mainExchange the main exchange to validate.
     * @param joiningExchange the joining exchange to validate the main exchange against.
     * @param jointype the type of join to use.
     * @param resultType what type we want the result to be of.
     * @param <T1> any type extending {@link LlamaBean}
     * @param <T2> any type extending {@link LlamaBean}
     * @return a call to {@link FilterValidateAgainstBeansProcessor#doExecuteProcess()} with passed in params.
     */
    public static <T1 extends  LlamaBean, T2 extends LlamaBean> Exchange filterValidateAgainst(Exchange mainExchange, Exchange joiningExchange, JoinType jointype, ResultType resultType) {
        return new <T1, T2>FilterValidateAgainstBeansProcessor(mainExchange, joiningExchange, jointype, resultType).doExecuteProcess();
    }

}
